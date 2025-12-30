import Box from "@mui/material/Box";

interface StyledLayoutWrapperProps {
    children: React.ReactNode;
    sx?: { flexGrow: number; alignItems: string; display: string; justifyContent: string }
}

export const StyledLayoutWrapper = (props: StyledLayoutWrapperProps) => {
    return (
        <Box
            sx={{
                //display: "flex",
                mr: 2, ml: 2, mb: 2,
                mt: {xs: 1, sm: 0},
                //bgcolor: (theme) => theme.palette.background.paper,
                ...props.sx,
                // minHeight: {
                //     xs: 'calc(100vh - 80px)',
                //     sm: 'calc(100vh - 80px)',
                //     md: 'calc(100vh - 136px)',
                // },
            }}
            //minHeight={'calc(100vh - 136px)'}
        >
            {props.children}
        </Box>
    );
}

