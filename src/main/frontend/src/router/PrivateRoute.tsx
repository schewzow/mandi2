import {Navigate, Outlet} from 'react-router-dom';
import {useAppSelector} from "../store/hooks/hooks.ts";

const PrivateRoute = () => {

    const {user} = useAppSelector((state) => state.auth);
    //console.log("PrivateRoute user", user);
    return user ? <Outlet/> : <Navigate to="login"/>;
}

export default PrivateRoute;