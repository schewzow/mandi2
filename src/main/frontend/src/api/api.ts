import axios from "axios";
import {toast} from "react-toastify";
import {createAndCheckErrorResponse, type RequestError} from "./api-error.ts";
import type {RequestResponse} from "./response.ts";

const api = axios.create({
    baseURL: `${import.meta.env.VITE_API_BASE_URL}`,
    withCredentials: true,
});

// Add a response interceptor
api.interceptors.response.use(
    function onFulfilled(response) {
        // Any status code that lie within the range of 2xx cause this function to trigger
        //console.log("Interceptor response: ", response);
        return response;
    },
    function onRejected(error) {
        // Any status codes that falls outside the range of 2xx cause this function to trigger
        if (error.response) {
            console.log(error.response);
            //const status = error.response.status;
            // Zeige Toast je nach Statuscode
            // if (status === 401) toast.error("Nicht autorisiert!");
            // else if (status === 404) toast.warning("Ressource nicht gefunden!");
            // else if (status >= 500) toast.error("Serverfehler!");
            // else toast.error(error.response.data?.message || "Ein Fehler ist aufgetreten.");

            //if(error.response?.data?.global)

            if (error.response?.data != null) {
                const x: RequestResponse<RequestError> = createAndCheckErrorResponse(error.response?.data);
                if (x.error !== null) {
                    if (Array.isArray(x.error.global) && x.error.global.length > 0) {
                        toast.error(x.error.global.map(e => "key: " + e.key + ", message: " + e.message).join("\n"));
                    }
                }
            } else {
                toast.error(error.response.data?.message || "Ein Fehler ist aufgetreten.");
            }
        } else {
            toast.error("Netzwerkfehler – keine Antwort vom Server!");
        }
        //console.log("Interceptor error: ", error);
        return Promise.reject(error);
    }
);

export default api;