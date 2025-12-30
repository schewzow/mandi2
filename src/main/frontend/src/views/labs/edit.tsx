import {StyledLayoutWrapper} from "../utils/styled-layout-wrapper.tsx";
import {Alert, Box, Grid} from "@mui/material";
import {useEntityForm} from "../../hooks/useEntityForm.ts";
import type {LaboratoryEntity} from "../../types/laboratory.ts";
import LaboratoryEntityApi, {PATH as EntityApiPath} from "../../api/labs/laboratory-entity-api.ts";
import {EntityTextField} from "../../components/Form/components/EntityTextField.tsx";
import {EntityNumericField} from "../../components/Form/components/EntityNumericField.tsx";
import {useParams} from "react-router-dom";
import {EntityDateField} from "../../components/Form/components/EntityDateField.tsx";
import {EntitySwitchField} from "../../components/Form/components/EntitySwitchField.tsx";
import type {UserEntity} from "../../types/user.ts";
import {EntitySelectField} from "../../components/Form/components/EntitySelectField.tsx";
import UserEntityApi from "../../api/user/user-entity-api.ts";
import {toDateTimeStringY} from "../../utils/date.ts";

const COMMON_TEXT_FIELD_PROPS = {size: "small" as const, variant: "outlined" as const};

export const LabsEditPage = () => {

    const params = useParams<{ uuid: string }>();
    const uuid = params.uuid ?? "create";

    const defaultValues: Partial<LaboratoryEntity> = {
        name: "Das ist der Name",
        shortName: "Kurzname",
        resultValue: 123.5,
        labDate: toDateTimeStringY((new Date()).toString()),
        labSwitchOn: true,
    };

    const {data, loading, setField, errors, globalErrors, states} = useEntityForm<LaboratoryEntity>({
        uuid,
        entityApi: LaboratoryEntityApi,
        entityPath: EntityApiPath,
        debounceMs: 400,
        defaultValues,
    });

    if (loading) return null;

    const commonProps = {data, errors, states, setField};

    return (
        <StyledLayoutWrapper>

            <Box sx={{
                padding: 2,
                borderRadius: 1,
                bgcolor: (theme) => theme.palette.background.paper,
                marginBottom: 2,
            }}>
                <Box>
                    {/*<Box component="form" onSubmit={(e) => e.preventDefault()} sx={{maxWidth: 520}}>*/}
                    {globalErrors.length > 0 && (
                        <Alert severity="error" sx={{mb: 2}}>
                            {globalErrors.map((g, i) => <div key={i}>{g}</div>)}
                        </Alert>
                    )}

                    {data ? (
                        <Grid container spacing={2} size={12}>
                            <Grid size={{xs: 12}}>
                                <EntityTextField<LaboratoryEntity, "name">
                                    {...commonProps}
                                    field="name"
                                    label="Name"
                                    i18nLabel="entities.laboratories.name"
                                    patchOnBlur
                                    fieldProps={COMMON_TEXT_FIELD_PROPS}
                                    // optional: normalize/compare to match backend trimming
                                    normalize={(v) => v?.trim()}
                                />
                            </Grid>
                            <Grid size={{xs: 12}}>
                                <EntityTextField<LaboratoryEntity, "shortName">
                                    {...commonProps}
                                    field="shortName"
                                    label="Short Name"
                                    i18nLabel="entities.laboratories.shortName"
                                    patchOnBlur
                                    fieldProps={COMMON_TEXT_FIELD_PROPS}
                                    // optional: normalize/compare to match backend trimming
                                    normalize={(v) => v?.trim()}
                                />
                            </Grid>
                            <Grid size={{xs: 12}}>
                                <EntityNumericField<LaboratoryEntity, "resultValue">
                                    {...commonProps}
                                    field="resultValue"
                                    label="Result Value"
                                    i18nLabel="common.resultValue"
                                    patchOnBlur
                                    fieldProps={COMMON_TEXT_FIELD_PROPS}
                                />
                            </Grid>
                            <Grid size={{xs: 12}}>
                                <EntityDateField<LaboratoryEntity, "labDate">
                                    {...commonProps}
                                    field="labDate"
                                    label="Lab Date"
                                    i18nLabel="entities.base.lastModifiedDate"
                                    patchOnBlur={false}
                                />
                            </Grid>
                            <Grid size={{xs: 12}}>
                                <EntitySwitchField<LaboratoryEntity, "labSwitchOn">
                                    {...commonProps}
                                    field="labSwitchOn"
                                    label="Lab Switch On"
                                    i18nLabel="entities.laboratories.labSwitchOn"
                                    //patchOnBlur={false}
                                />
                            </Grid>
                            <Grid size={{xs: 12}}>
                                <EntitySwitchField<LaboratoryEntity, "labSwitchOff">
                                    {...commonProps}
                                    field="labSwitchOff"
                                    label="Lab Switch Off"
                                    i18nLabel="entities.laboratories.labSwitchOff"
                                    //patchOnBlur={false}
                                />
                            </Grid>
                            <Grid size={{xs: 12}}>
                                <EntitySelectField<UserEntity, "firstname", LaboratoryEntity, "labUser">
                                    {...commonProps}
                                    refFieldEntityApi={UserEntityApi}
                                    showAcknowledge
                                    refField={"firstname"}
                                    refFieldValue={data.labUser}
                                    field={"labUser"}
                                    i18nLabel="entities.users.firstname"
                                />
                            </Grid>
                        </Grid>

                    ) : (
                        <Alert severity="info">No entity found.</Alert>
                    )}
                </Box>
            </Box>

        </StyledLayoutWrapper>);
}