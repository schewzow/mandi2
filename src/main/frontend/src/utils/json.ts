/**
 * JSON Tool collection
 */

/**
 * This method creates a flat object out of a nested json object. It uses the real path of the original
 * object as a key for the value. Returnvalue is the flat new object, obj won't be touched.
 *
 * @param obj A nested JSON object
 * @param path Base path, usually just called internally for recursion
 */
export const flattenJSON = (obj: object, path: string = ""): object => {

    if (!(obj instanceof Object)) return {[path.replace(/\.$/g, "")]: obj};

    return Object.keys(obj)
        .reduce(
            (output, key: string) => {
                return obj instanceof Array ?
                    {...output, ...flattenJSON((obj as any)[key], `${path}[${key}].`)} :
                    {...output, ...flattenJSON((obj as any)[key], `${path}${key}.`)};
            }
            ,
            {}
        );
};
