import {useState} from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import {Link, useNavigate} from "react-router";
import {Pages} from "../../router/pages.ts";
import {IconButton, Menu, MenuItem} from "@mui/material";
import Box from "@mui/material/Box";
import MenuIcon from '@mui/icons-material/Menu';
import Button from "@mui/material/Button";
import {useAppDispatch, useAppSelector} from "../../store/hooks/hooks.ts";
import {logoutUser} from "../../store/reducers/authSlice.ts";
import type {UserAuth} from "../../store/UserAuth.ts";


function Navbar() {

    const title: string = "MANDI";
    const user: UserAuth | null = useAppSelector((state) => state.auth.user)
    const dispatch = useAppDispatch()
    const navigate = useNavigate();

    const isAdmin = user && user?.roles?.includes("ADMIN");

    const [anchorElNav, setAnchorElNav] = useState<null | HTMLElement | undefined>(null);

    // if (!user) {
    //     return <SpinnerLoading/>
    // }

    // if (Env.API_STAGE_CONFIG === "DEV") {
    //     console.log("User: ", user);
    // }

    const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorElNav(event.currentTarget);
    };

    const handleCloseNavMenu = (to: string) => {
        setAnchorElNav(null);
        if (to === "logout") {
            dispatch(logoutUser(navigate));
        } else if (to !== null && to !== '') {
            navigate(to);
        }
    };

    return (
        <>
            <AppBar position="fixed"
                    sx={{
                        bgcolor: '#0e76a8',
                        borderBottomLeftRadius: 7,
                        borderBottomRightRadius: 7
                    }}
            >
                <Toolbar variant="dense" sx={{justifyContent: 'space-between'}}>
                    <Typography
                        variant="h6"
                        noWrap
                        component={Link}
                        to="/home"
                        sx={{
                            mr: 10,
                            display: {xs: 'none', md: 'flex'},
                            color: 'inherit',
                            textDecoration: 'none',
                        }}
                    >
                        {title}
                    </Typography>


                    <Box sx={{display: {xs: 'flex', md: 'none'}}}>
                        <IconButton
                            size="large"
                            aria-label="account of current user"
                            aria-controls="menu-appbar"
                            aria-haspopup="true"
                            onClick={handleOpenNavMenu}
                            color="inherit"
                        >
                            <MenuIcon/>
                        </IconButton>
                        <Menu
                            id="menu-appbar"
                            anchorEl={anchorElNav}
                            anchorOrigin={{
                                vertical: 'bottom',
                                horizontal: 'left',
                            }}
                            keepMounted
                            transformOrigin={{
                                vertical: 'top',
                                horizontal: 'left',
                            }}
                            open={Boolean(anchorElNav)}
                            onClose={() => handleCloseNavMenu('')}
                            sx={{display: {xs: 'block', md: 'none'}}}
                        >
                            {user ?
                                Pages.map((page) => (
                                    <MenuItem key={page.text} onClick={() => handleCloseNavMenu(page.href)}>
                                        <Typography sx={{textAlign: 'center'}}>{page.text}</Typography>
                                    </MenuItem>
                                ))
                                :
                                null
                            }
                            {!user ?
                                <MenuItem
                                    onClick={() => handleCloseNavMenu('/login')}
                                >
                                    <Typography sx={{textAlign: 'center'}}>Login</Typography>
                                </MenuItem>
                                :
                                <MenuItem
                                    onClick={() => handleCloseNavMenu('logout')}
                                >
                                    <Typography sx={{textAlign: 'center'}}>Log out</Typography>
                                </MenuItem>
                            }
                        </Menu>
                    </Box>

                    <Box sx={{flexGrow: 1, display: {xs: 'flex', md: 'none'}}}></Box>

                    <Button
                        component={Link}
                        to="/home"
                        sx={{color: 'white', display: {xs: 'flex', md: 'none'}}}
                    >
                        <Typography
                            noWrap
                            variant="h6"
                            textTransform="none"
                        >
                            {title}
                        </Typography>
                    </Button>

                    <Box sx={{flexGrow: 1, display: {xs: 'flex', md: 'none'}}}></Box>

                    <Box sx={{flexGrow: 1, display: {xs: 'none', md: 'flex'}}}>
                        {user ?
                            Pages.map((page) => {
                                if (page.admin && !isAdmin) {
                                    return (
                                        <></>
                                    );
                                } else {
                                    return (
                                        <Button
                                            key={page.text}
                                            onClick={() => handleCloseNavMenu(page.href)}
                                            sx={{color: 'white', display: 'block'}}
                                        >
                                            {page.text}
                                        </Button>
                                    )
                                }
                            })
                            // Pages.map((page) => (
                            //     <Button
                            //         key={page.text}
                            //         onClick={() => handleCloseNavMenu(page.href)}
                            //         sx={{color: 'white', display: 'block'}}
                            //     >
                            //         {page.text}
                            //     </Button>
                            // ))
                            : <></>
                        }

                        <Box sx={{flexGrow: 1, display: {xs: 'none', md: 'flex'}}}></Box>

                        {!user ?
                            <Button
                                onClick={() => handleCloseNavMenu('/login')}
                                sx={{color: 'white'}}
                            >
                                Login
                            </Button>
                            :
                            <Button
                                onClick={() => handleCloseNavMenu('logout')}
                                sx={{color: 'white', display: 'block'}}
                            >
                                Log out
                            </Button>
                        }
                    </Box>


                </Toolbar>
            </AppBar>
            <Toolbar/>
        </>
    );
}

export default Navbar;
