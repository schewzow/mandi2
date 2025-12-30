import axios from "axios";
import {toast} from "react-toastify";
import {createAndCheckErrorResponse, type RequestError} from "./api-error.ts";
import type {RequestResponse} from "./response.ts";

let logoutHandler: (() => void) | null = null;

// Function to "inject" the logout logic from the React side
export const injectLogoutHandler = (handler: () => void) => {
    logoutHandler = handler;
};

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
    async function onRejected(error) {
        // Any status codes that falls outside the range of 2xx cause this function to trigger
        if (error.response) {
            //console.log(error.response);
            const originalConfig = error.config;
            // If 401 occurs and we haven't tried to refresh yet
            if (error.response?.status === 401 && !originalConfig.url?.includes("/auth/refreshtoken")) {

                if (!originalConfig._retry) {
                    originalConfig._retry = true;

                    try {
                        // Use the global axios or the api instance to call refresh
                        await api.post("/auth/refreshtoken", {}, {});

                        // 3. FIX: Use 'api' here to retry the request
                        return api(originalConfig);
                    } catch (_error) {
                        // Handle session expiry
                        // Refresh token failed or is missing
                        console.error("Session expired. Logging out.");

                        // Call the injected handler instead of using the store directly
                        if (logoutHandler) {
                            logoutHandler();
                        } else {
                            window.location.href = "/login";
                        }

                        return Promise.reject(_error);
                    }
                }
            }
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
                        toast.error(x.error.global.map(e => e.message).join("\n"));
                        //toast.error(x.error.global.map(e => "key: " + e.key + ", message: " + e.message).join("\n"));
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