import '../../assets/css/modal.css'
import {Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from "@mui/material";
import SaveAck from "./SaveAck.tsx";
import Button from "@mui/material/Button";
import {ColorPrimary, ColorWarning} from "../../utils/colors.ts";
import {useEffect, useState} from "react";
import type {RequestResponse} from "../../api/response.ts";
import type {BaseEntity} from "../../types/base-entity.ts";
import type {ActionType} from "../../types/action-type.ts";

interface EntityDialogBaseProps {
    entity: string;
    actionType: ActionType;
    open: boolean;
    saveChanges: () => Promise<RequestResponse<BaseEntity>>;
    deleteEntity: () => Promise<RequestResponse<BaseEntity>>;
    closeDialog: (updated: boolean) => void;
    headerBackground?: string;
}

export const EntityDialogBase: React.FC<React.PropsWithChildren<EntityDialogBaseProps>>
    = ({entity, actionType, open, saveChanges, deleteEntity, closeDialog, headerBackground, children}) => {

    const [saving, setSaving] = useState<boolean>(false);
    const [httpError, setHttpError] = useState<string>("");

    useEffect(() => {
        if (saving) {
            setTimeout(() => {
                setSaving(false);
                closeDialog(true);
            }, 300);
            return;
        }
    }, [saving]);

    async function onSaveChanges() {

        let response: RequestResponse<BaseEntity>;

        if (actionType === "DELETE") {
            response = await deleteEntity();
        }
        else {
            response = await saveChanges();
        }

        if (response.status === "success") {
            setSaving(true);
        } else {
            if (response.error) {
                setHttpError("error......... fix me");
            }
        }
    }

    return (
        <Dialog
            maxWidth={false}
            open={open}
            onClose={() => closeDialog(false)}
        >
            <DialogTitle sx={{background: `${headerBackground}`}}>
                {entity}
                <SaveAck isSaving={saving}/>
            </DialogTitle>
            <DialogContent>
                {children}
            </DialogContent>

            {httpError ?
                <DialogContentText color="error">
                    {httpError}
                </DialogContentText>
                : <></>
            }

            <DialogActions>
                <Button onClick={() => closeDialog(false)}>
                    Cancel
                </Button>
                <Button
                    color={actionType === "DELETE" ? ColorWarning : ColorPrimary}
                    onClick={onSaveChanges}
                >
                    {(() => {
                        switch (actionType) {
                            case "CREATE":
                                return (<span>Add {entity}</span>);
                            case "DELETE":
                                return (<span>Delete {entity}</span>);
                            case "UPDATE":
                                return (<span>Save</span>);
                            default:
                                return (<></>);
                        }
                    })()}
                </Button>
            </DialogActions>
        </Dialog>
    );
};
