import {createRoot} from 'react-dom/client'
import './App.css'
import App from './App.tsx'
import {Provider} from 'react-redux'
import {BrowserRouter} from "react-router-dom"
import store from "./store/reducers/Store.ts";
import {createTheme, CssBaseline, ThemeProvider} from "@mui/material";
import {CookiesProvider} from "react-cookie";
import {injectLogoutHandler} from "./api/api.ts";
import {logoutUser} from "./store/reducers/authSlice.ts";
import {Env} from "./Env.ts";

const theme = createTheme({
    palette: {primary: {main: "#0e76a8"},}
});

// Setup the link between Redux and Axios
injectLogoutHandler(() => {
    store.dispatch(logoutUser());
    window.location.href = `${Env.LOGIN_URL}`;
});

createRoot(document.getElementById('root')!).render(
    <Provider store={store}>
        <CookiesProvider>
            <BrowserRouter basename={'/mandi'}>
                <ThemeProvider theme={theme}>
                    <CssBaseline/>
                    <App/>
                </ThemeProvider>
            </BrowserRouter>
        </CookiesProvider>
    </Provider>,
)