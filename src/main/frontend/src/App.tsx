import {Box} from "@mui/material";
import AppRoutes from "./router/AppRoutes.tsx";
import Navbar from "./views/navbar/Navbar.tsx";
import {ToastContainer} from "react-toastify";
import {IntlProvider} from "react-intl";
import {useAppSelector} from "./store/hooks/hooks.ts";
import type {UserAuth} from "./store/UserAuth.ts";
import {flattenJSON} from "./utils/json.ts";
import intl_de from "./i18n/translations/de.ts"
import intl_en from "./i18n/translations/en.ts"
import {LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import 'dayjs/locale/de';
import 'dayjs/locale/en';

function App() {

    //const dispatch = useDispatch();

    // function handleGetHelloInfo() {
    //     dispatch(getHelloInfo());
    // }
    const user: UserAuth | null = useAppSelector((state) => state.auth.user)

    //console.log("App user", user);

    let userLocale: string = "en";
    let messages = flattenJSON(intl_en(), "") as Record<string, string>;//English;
    switch (user?.language) {
        case "de-DE":
            userLocale = "de";
            messages = flattenJSON(intl_de(), "") as Record<string, string>; //German;
            break;
    }

    // const test = flattenJSON(intl_de(), "andrej");
    //console.log(messages);

    return (
        <IntlProvider
            locale={userLocale}
            messages={messages}
        >
            <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale={userLocale}>
                <Box className="App" style={{display: "flex", flexDirection: "column", height: "100vh"}}>
                    <Navbar/>
                    <AppRoutes/>

                    {/*<Button variant="contained" sx={{marginTop: 2}}*/}
                    {/*        onClick={handleGetHelloInfo}>*/}
                    {/*    Get Hello Info*/}
                    {/*</Button>*/}
                </Box>
            </LocalizationProvider>
            <ToastContainer position="top-right" autoClose={3000}/>
        </IntlProvider>

    )
}

export default App