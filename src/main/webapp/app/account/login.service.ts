interface Emitter {
  emit: (event: string, ...args: any[]) => void;
}

export default class LoginService {
  private readonly emit: Emitter["emit"];

  constructor({ emit }: Emitter) {
    this.emit = emit;
  }

  public openLogin(): void {
    this.emit("bv::show::modal", "login-page");
  }

  public hideLogin(): void {
    this.emit("bv::hide::modal", "login-page");
  }
}
