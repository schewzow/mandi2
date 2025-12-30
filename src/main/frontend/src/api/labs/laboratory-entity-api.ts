import EntityAPI from "../entity-api.ts";
import type {LaboratoryEntity} from "../../types/laboratory.ts";

export const PATH = "labs";

export default {

    ...EntityAPI<LaboratoryEntity>(PATH),

    // fetchPaged: async (
    //     sortModel: GridSortModel | undefined,
    //     page: number,
    //     size: number,
    //     filter: string
    // ): Promise<ResponseApi<LaboratoryEntity>> => {
    //
    //     let sort = "";
    //     if (sortModel && sortModel.length > 0) {
    //         sort = `${sortModel[0].field},${sortModel[0].sort}`;
    //     }
    //
    //     return await getPaginated<LaboratoryEntity>(
    //         {
    //             path: `${getSearchAPIPath(PATH)}`,
    //             urlParams: {
    //                 filter,
    //                 page,
    //                 size,
    //                 sort,
    //             },
    //         },
    //     );
    // },
};