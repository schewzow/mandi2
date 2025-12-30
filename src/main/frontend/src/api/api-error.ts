import type {RequestResponse} from "./response.ts";

export interface ErrorType
{
    key: string;
    message: string;
}

export interface RequestError
{
    global: ErrorType[];
    fields: {
        [key: string]: ErrorType[];
    };
}

export const createGenericErrorType = (): ErrorType => ({
    key: "fe.generic.unknownError",
    message: "Unknown request error occurred",
});

/**
 * This function validates the received response and creates an error response
 * @param responseEntity The response.entity to create the error for this could also be anything as the catch can catch anything
 */
export function createAndCheckErrorResponse<Response>(
    responseEntity: any,
): RequestResponse<Response>
{
    //console.log(responseEntity);
    // Only further check if we got an entity
    if (responseEntity != null)
    {
        // Try to make the default path the shortest one
        if (typeof responseEntity === "object")
        {
            // In this case we have a response that is fairly close to valid (subtypes of array and fields are not checked)
            if (Array.isArray(responseEntity.global) && responseEntity.fields != null && typeof responseEntity.fields === "object")
            {
                //console.log("Almost a valid response");
                return {
                    status: "error",
                    error: responseEntity as RequestError,
                    data: null,
                };
            }

            const global = [];

            // If we have data in the response add it
            if (Array.isArray(responseEntity.global))
            {
                global.push(responseEntity.global);
            }
            // In this case global is an unwrapped ErrorType
            else if (typeof responseEntity.global === "object" && typeof responseEntity.global.message === "string")
            {
                global.push({
                    key: "fe.generic.onlyMessageNoKey", // Set a backup key if global doesn't have one
                    ...responseEntity.global,
                });
            }
            // If both global and field are not conform then add the unknown error message
            else if (typeof responseEntity.fields !== "object" || responseEntity.fields == null)
            {
                global.push(createGenericErrorType());
            }

            // In this case we build a partial fallback and override misaligned types.
            return {
                status: "error",
                error: {
                    ...responseEntity, // Let's add to original data
                    // Override properties to enforce types
                    fields: {
                        // If fields is an object spread the values into the fields property
                        ...(typeof responseEntity.fields === "object" ? responseEntity.fields : undefined),
                    },
                    global,
                },
                data: null,
            };
        }

        // The response is a non-empty string so lets wrap it
        if (typeof responseEntity === "string" && responseEntity !== "")
        {
            return {
                status: "error",
                error: {
                    global: [{
                        key: "fe.generic.stringResponseWrapped",
                        message: responseEntity,
                    }],
                    fields: {},
                },
                data: null,
            };
        }
    }

    // Throw a generic error as we can't really do anything
    return {
        status: "error",
        error: {
            global: [createGenericErrorType()],
            fields:  {},
        },
        data: null,
    };
}