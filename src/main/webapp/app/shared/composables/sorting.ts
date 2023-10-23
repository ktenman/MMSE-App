import { ref } from "vue";

export function useSorting(defaultPropOrder = "id", defaultReverse = false) {
  const propOrder = ref(defaultPropOrder);
  const reverse = ref(defaultReverse);

  const changeOrder = (newOrder: string) => {
    if (propOrder.value === newOrder) {
      reverse.value = !reverse.value;
    } else {
      reverse.value = false;
    }
    propOrder.value = newOrder;
  };

  const getSort = (): Array<any> => {
    const result = [propOrder.value + "," + (reverse.value ? "desc" : "asc")];
    if (propOrder.value !== "id") {
      result.push("id");
    }
    return result;
  };

  return { propOrder, reverse, changeOrder, getSort };
}
