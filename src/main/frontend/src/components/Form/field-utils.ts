export type StringKeys<T> = {
    [K in keyof T]-?:
    T[K] extends string ? K :
        T[K] extends number ? K :
            T[K] extends boolean ? K :
                T[K] extends object | null ? K :
                never;
}[keyof T];

//export type StringValue<T, K extends keyof T> = T[K] extends string ? T[K] : never;

export type StringValue<T, K extends StringKeys<T>> = Extract<T[K], string | number | boolean>;

