const { VITE_LOGIN_URL, VITE_BASE_URL, VITE_API_BASE_URL, VITE_STAGE_CONFIG, ...otherViteConfig } = import.meta.env;

export const Env = {
    LOGIN_URL: VITE_LOGIN_URL as string,
    BASE_URL: VITE_BASE_URL as string,
    API_BASE_URL: VITE_API_BASE_URL as string,
    API_STAGE_CONFIG: VITE_STAGE_CONFIG as string,
    __vite__: otherViteConfig,
};

// This is one of the few places where I recommend adding a `console.log` statement
// To make it easy to figure out the frontend environment config at any moment
console.log(Env);