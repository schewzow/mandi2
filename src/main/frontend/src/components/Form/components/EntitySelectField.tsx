import useFetchEntityList from "../../../hooks/useFetchEntityList.ts";
import * as React from "react";
import type {BaseEntity} from "../../../types/base-entity.ts";
import type {EntityAPIType} from "../../../api/entity-api.ts";
import {Autocomplete, InputAdornment, TextField} from "@mui/material";
import type {StringKeys, StringValue} from "../field-utils.ts";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import WarningAmberIcon from "@mui/icons-material/WarningAmber";
import {useEntityField} from "../../../hooks/useEntityField.ts";
import {useSaveAcknowledgment} from "../../../hooks/useSaveAcknowledgment.ts";
import {FormattedMessage} from "react-intl";
import type {EntityFieldProps} from "../EntityFieldProps.ts";

export interface EntitySelectFieldProps<
    FieldEntity extends BaseEntity,
    K extends StringKeys<FieldEntity>,
    ParentEntity extends BaseEntity,
    PK extends StringKeys<ParentEntity>
> extends EntityFieldProps<ParentEntity, PK> {
    refFieldEntityApi: EntityAPIType<FieldEntity>,
    refField: K,
    refFieldValue: FieldEntity | null,
};

//type LabelOption = {label: string, id: number};

export function EntitySelectField<
    FieldEntity extends BaseEntity,
    K extends StringKeys<FieldEntity>,
    ParentEntity extends BaseEntity,
    PK extends StringKeys<ParentEntity>,
>(props: EntitySelectFieldProps<FieldEntity, K, ParentEntity, PK>) {

    const {
        showAcknowledge,
        refFieldEntityApi,
        refField,
        refFieldValue,
        data,
        field,
        errors, states, setField, patchOnBlur, //, loading, normalize, compare,
        i18nLabel,
    } = props;

    const {error, helperText, disabled, onChange, state} = useEntityField<ParentEntity, PK>({
        data,
        errors,
        states,
        setField,
        field,
        patchOnBlur,
        // loading,
        // normalize,
        // compare,
    });

    //const [query, setQuery] = useState<string>("");
    const [nextValue, setNextValue] = React.useState<FieldEntity | null>(refFieldValue);
    //const [inputValue, setInputValue] = React.useState("");
    //const [storedParentEntity, setStoredParentEntity] = useState<ParentEntity>(parentEntity);

    const {isFetching, items} = useFetchEntityList<FieldEntity>({
        entityApi: refFieldEntityApi,
        filter: "",// query,
    });

    async function handleOnChange(newValue: FieldEntity | null) {
        setNextValue(newValue);
        //setState("UNDEFINED");

        onChange(newValue?.uuid as StringValue<ParentEntity, PK>);

        // const response = await parentEntityApi.patch({
        //     //uuid: parentEntity.uuid, data: parentEntity
        //     uuid: data.uuid, data: {[field]: newValue?.uuid + "_"} as Partial<ParentEntity>
        // });
        // if (response.status === "error") {
        //     setState("ERROR");
        //     console.log("ERROR", response);
        // } else {
        //     setState("SUCCESS");
        // }
    }

    // const options: LabelOption[] = items.map((item, id: number) => (
    //     {label: item[field] as string, id: id})
    // );
    // console.log(options);

    const showAck = useSaveAcknowledgment(state, showAcknowledge);

    return (
        <Autocomplete
            disabled={disabled}
            size="small"
            fullWidth={true}
            options={items}
            value={nextValue}
            getOptionKey={(option) => option.uuid}
            getOptionLabel={(option) => option[refField] as string}
            loading={isFetching}
            onChange={(_, newValue) => handleOnChange(newValue)}
            // inputValue={inputValue}
            // onInputChange={(event, newInputValue) => {
            //     setInputValue(newInputValue);
            // }}
            renderInput={(params) =>
                <TextField
                    {...params}
                    label={i18nLabel ? <FormattedMessage id={i18nLabel}/> : String(refField)}
                    helperText={helperText}
                    error={Boolean(error)}
                    // slotProps={{
                    //     input: {
                    //         endAdornment: (
                    //             <InputAdornment position="end">
                    //                 <CheckCircleIcon color="success" fontSize="small"/>
                    //             </InputAdornment>
                    //         ),
                    //     },
                    // }}
                    // this is fully okay as long as MUI Autocomplete is relying on it by themselves!
                    InputProps={{
                        ...params.InputProps,
                        endAdornment: (
                            <>
                                {showAck && (
                                    <InputAdornment position="end" sx={{mr: 0.5}}>
                                        {state === "SUCCESS" ? (
                                            <CheckCircleIcon color="success" fontSize="small"/>
                                        ) : (
                                            <WarningAmberIcon color="error" fontSize="small"/>
                                        )}
                                    </InputAdornment>
                                )}
                                {params.InputProps.endAdornment}
                            </>
                        ),
                    }}
                />
            }
        />

        // <Box position="relative">
        //     <Autocomplete
        //         sx={{position: "relative"}}
        //         size="small"
        //         fullWidth={true}
        //         options={items}
        //         value={nextValue}
        //         getOptionKey={(option) => option.uuid}
        //         loading={isFetching}
        //         onChange={(_, newValue: FieldEntity | null) => {
        //             handleOnChange(newValue);
        //         }}
        //         // inputValue={inputValue}
        //         // onInputChange={(event, newInputValue) => {
        //         //     setInputValue(newInputValue);
        //         // }}
        //         renderInput={(params) =>
        //             <TextField
        //                 {...params}
        //                 // slotProps={{
        //                 //     input: {
        //                 //         endAdornment: (
        //                 //             <InputAdornment position="end">
        //                 //                 <CheckCircleIcon color="success" fontSize="small"/>
        //                 //             </InputAdornment>
        //                 //         ),
        //                 //     },
        //                 // }}
        //             />
        //         }
        //         getOptionLabel={(option) => option[field] as string}
        //         // onChange={(event, newValue) => {
        //         //     setQuery(newValue?.name ?? "");
        //         // }}
        //     />
        //     <Box
        //         position="absolute"
        //         right={32}
        //         top="50%"
        //         sx={{transform: "translateY(-50%)", pointerEvents: "none"}}
        //     >
        //         {showAck ? (
        //             state === "SUCCESS" ? (
        //                 <CheckCircleIcon color="success" fontSize="small"/>
        //             ) : (
        //                 <WarningAmberIcon color="error" fontSize="small"/>
        //             )
        //         ) : null}
        //     </Box>
        // </Box>
    );
}