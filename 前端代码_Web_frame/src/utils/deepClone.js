export const deepClone = (data) => {
    if (!data || !(data instanceof Object) || (typeof data == "function")) {
        return data || undefined;
    }
    var constructor = data.constructor;
    var result = new constructor();
    for (var key in data) {
        if (data.hasOwnProperty(key)) {
            result[key] = deepClone(data[key]);
        }
    }
    return result;
}