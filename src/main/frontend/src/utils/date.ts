export const toDateTimeString = (d: string) => {
    const date = new Date(Date.parse(d));

    return `${("00" + date.getDate()).slice(-2)}-${("00" + (date.getMonth() + 1)).slice(-2)}-${date.getFullYear()}`
        + ` ${("00" + date.getHours()).slice(-2)}:${("00" + date.getMinutes()).slice(-2)}:${("00" + date.getSeconds()).slice(-2)}`;

    // return `${date.getFullYear()}-${("00" + (date.getMonth() + 1)).slice(-2)}-${("00" + date.getDate()).slice(-2)}`
    //     + ` ${("00" + date.getHours()).slice(-2)}:${("00" + date.getMinutes()).slice(-2)}:${("00" + date.getSeconds()).slice(-2)}`;
};

export const toDateString = (d: string) => {
    const date = new Date(Date.parse(d));
    return `${("00" + date.getDate()).slice(-2)}-${("00" + (date.getMonth() + 1)).slice(-2)}-${date.getFullYear()}`;
    //return `${date.getFullYear()}-${("00" + (date.getMonth() + 1)).slice(-2)}-${("00" + date.getDate()).slice(-2)}`;
};

export const toDateStringDe = (d: string) => {
    const date = new Date(Date.parse(d));
    return `${("00" + date.getDate()).slice(-2)}.${("00" + (date.getMonth() + 1)).slice(-2)}.${date.getFullYear()}`;
    //return `${date.getFullYear()}-${("00" + (date.getMonth() + 1)).slice(-2)}-${("00" + date.getDate()).slice(-2)}`;
};

export const toDateStringY = (d: string) => {
    const date = new Date(Date.parse(d));
    return `${("0000" + date.getFullYear()).slice(-4)}-${("00" + (date.getMonth() + 1)).slice(-2)}-${("00" + date.getDate()).slice(-2)}`;
};

export const toDbDateString = (d: string) => {
    const dateString: string = toDateStringY(d);
    return dateString + "T00:00:00";
}

export const getLabelFromReportDate = (date: number): string => {
    const sval: string = "" + date;
    return "" + sval.slice(0, 4) + "." + sval.slice(4, 6) + "." + sval.slice(6);
}

