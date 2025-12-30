import {LogIn} from "../views/auth/LogIn.tsx";
import {HomePage} from "../views/home/HomePage.tsx";
import {Route, Routes} from "react-router-dom";
import PrivateRoute from "./PrivateRoute.tsx";
import NotFound from "../views/utils/NotFound.tsx";
import {LabsPage} from "../views/labs";
import {LabsEditPage} from "../views/labs/edit.tsx";
import route from "./routes";
// import LaboratoryDetail from "../views/Laboratory/LaboratoryDetail.tsx";

const AppRoutes = () => {
    return (
        <Routes>
            <Route index element={<HomePage/>}/>
            <Route path='home' element={<HomePage/>}/>
            <Route element={<PrivateRoute/>}>
                {/* PRIVATE_ROUTES_BEGIN */}
                <Route path={"labs"} element={<LabsPage/>}/>
                {/*<Route path={"labs/:uuid"} element={<LabsEditPage/>}/>*/}
                <Route path={route.laboratories.detail} element={<LabsEditPage/>}/>
                {/*<Route path={route.laboratories.detail} element={<LaboratoryDetail/>} />*/}
                {/* PRIVATE_ROUTES_END */}
            </Route>
            <Route path='login' element={<LogIn/>}/>
            <Route path='*' element={<NotFound/>}/>
        </Routes>
    );
};

export default AppRoutes;