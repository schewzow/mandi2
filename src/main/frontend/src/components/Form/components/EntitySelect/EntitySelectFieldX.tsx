//useFetchEntityList

import useFetchEntityList from "../../../../hooks/useFetchEntityList.ts";
import * as React from "react";
import {useState} from "react";
import type {BaseEntity} from "../../../../types/base-entity.ts";
import type {EntityAPIType} from "../../../../api/entity-api.ts";
import {Autocomplete, InputAdornment, TextField} from "@mui/material";
import type {StringKeys} from "../../field-utils.ts";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import type {StateType} from "../../../../hooks/useEntityForm.ts";
import WarningAmberIcon from "@mui/icons-material/WarningAmber";
import {useSaveAcknowledgment} from "../../../../hooks/useSaveAcknowledgment.ts";

export type EntitySelectFieldPropsX<
    FieldEntity,
    K extends StringKeys<FieldEntity>,
    ParentEntity,
    PK extends StringKeys<ParentEntity>
> = {
    showAcknowledge?: boolean,
    fieldEntityApi: EntityAPIType<FieldEntity>,
    parentEntityApi: EntityAPIType<ParentEntity>,
    field: K,
    value: FieldEntity | null,
    parentEntity: ParentEntity,
    parentEntityField: PK,
    //handleOnChange: (newValue: FieldEntity | null) => void,
};

//type LabelOption = {label: string, id: number};

export function EntitySelectFieldX<
    FieldEntity extends BaseEntity,
    K extends StringKeys<FieldEntity>,
    ParentEntity extends BaseEntity,
    PK extends StringKeys<ParentEntity>,
>(props: EntitySelectFieldPropsX<FieldEntity, K, ParentEntity, PK>) {

    const {showAcknowledge, fieldEntityApi, parentEntityApi, field, value, parentEntity, parentEntityField} = props;
    //const [query, setQuery] = useState<string>("");
    const [nextValue, setNextValue] = React.useState<FieldEntity | null>(value);
    //const [inputValue, setInputValue] = React.useState("");
    //const [storedParentEntity, setStoredParentEntity] = useState<ParentEntity>(parentEntity);

    const {isFetching, items} = useFetchEntityList<FieldEntity>({
        entityApi: fieldEntityApi,
        filter: "", //query,
    });

    async function handleOnChange(newValue: FieldEntity | null) {
        setNextValue(newValue);
        setState("UNDEFINED");

        // setStoredParentEntity(
        //     (prev) => (
        //         console.log("PREV ", prev),
        //         prev === null ? prev : {
        //             ...prev, ["labUser"]: newValue?.uuid ?? null }
        //     )
        // );

        //const out: Partial<ParentEntity> = {[parentEntityField]: newValue?.uuid};
        //handleOnChange(newValue);
        const response = await parentEntityApi.patch({
            //uuid: parentEntity.uuid, data: parentEntity
            uuid: parentEntity.uuid, data: {[parentEntityField]: newValue?.uuid + "_"} as Partial<ParentEntity>
        });
        if (response.status === "error") {
            setState("ERROR");
            console.log("ERROR", response);
        } else {
            setState("SUCCESS");
        }
        //console.log(response);
    }

    // const options: LabelOption[] = items.map((item, id: number) => (
    //     {label: item[field] as string, id: id})
    // );
    // console.log(options);

    const [state, setState] = useState<StateType>("UNDEFINED");

    const showAck = useSaveAcknowledgment(state, showAcknowledge);

    return (
        <Autocomplete
            size="small"
            fullWidth={true}
            options={items}
            value={nextValue}
            getOptionKey={(option) => option.uuid}
            getOptionLabel={(option) => option[field] as string}
            loading={isFetching}
            onChange={(_, newValue) => handleOnChange(newValue)}
            // inputValue={inputValue}
            // onInputChange={(event, newInputValue) => {
            //     setInputValue(newInputValue);
            // }}
            renderInput={(params) =>
                <TextField
                    {...params}
                    label={String(field)}
                    helperText={showAck ? (state === "SUCCESS" ? "Success" : "Error") : undefined}
                    error={state === "ERROR"}
                    // slotProps={{
                    //     input: {
                    //         endAdornment: (
                    //             <InputAdornment position="end">
                    //                 <CheckCircleIcon color="success" fontSize="small"/>
                    //             </InputAdornment>
                    //         ),
                    //     },
                    // }}
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