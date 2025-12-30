import type {OverridableStringUnion} from "@mui/types";
import type {ButtonPropsColorOverrides} from "@mui/material";

export const ColorWarning: OverridableStringUnion<
    'inherit' | 'primary' | 'secondary' | 'success' | 'error' | 'info' | 'warning',
    ButtonPropsColorOverrides
> = 'warning';

export const ColorPrimary: OverridableStringUnion<
    'inherit' | 'primary' | 'secondary' | 'success' | 'error' | 'info' | 'warning',
    ButtonPropsColorOverrides
> = 'primary';

