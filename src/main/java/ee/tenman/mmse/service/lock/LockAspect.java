package ee.tenman.mmse.service.lock;

import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LockAspect {

    private final Logger log = LoggerFactory.getLogger(LockAspect.class);
    private final ExpressionParser parser = new SpelExpressionParser();
    @Resource
    private LockService lockService;

    @Around("@annotation(lock)")
    public Object aroundLockedMethod(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
        if (lock.key().isBlank()) {
            throw new IllegalArgumentException("Lock key cannot be empty");
        }
        String lockKey = getKey(lock.key(), joinPoint);
        long timeoutMillis = lock.timeoutMillis();
        if (lock.retry()) {
            lockService.acquireLock(lockKey, timeoutMillis);
        } else {
            boolean lockAcquired = lockService.tryAcquireLock(lockKey, timeoutMillis);
            if (!lockAcquired) {
                throw new IllegalStateException("Unable to acquire lock for identifier: " + lockKey);
            }
        }
        log.debug("Lock acquired for key {} with lock key {}", lock.key(), lockKey);
        try {
            return joinPoint.proceed();
        } finally {
            lockService.releaseLock(lockKey);
            log.debug("Lock released for key {} with lock key {}", lock.key(), lockKey);
        }
    }

    private String getKey(String keyExpression, ProceedingJoinPoint joinPoint) {
        if (keyExpression.startsWith("'") && keyExpression.endsWith("'")) {
            return keyExpression.substring(1, keyExpression.length() - 1);
        }
        if (keyExpression.startsWith("#")) {
            keyExpression = keyExpression.substring(1);
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        if (signature == null) {
            throw new IllegalArgumentException("No argument found in method signature");
        }

        if (!keyExpression.contains(".")) {
            throw new IllegalArgumentException("No nested key found in key expression");
        }
        Object[] args = joinPoint.getArgs();
        String[] keys = keyExpression.split("\\.");
        String nestedKey = keys[1];
        EvaluationContext context = new StandardEvaluationContext(args[0]);
        Expression expression = parser.parseExpression(nestedKey);
        return expression.getValue(context, String.class);
    }
}
