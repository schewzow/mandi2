import {useEffect, useState} from "react";
import type {StateType} from "./useEntityForm.ts";

export function useSaveAcknowledgment(state: StateType | undefined, showAcknowledge: boolean = true) {
    const [showAck, setShowAck] = useState(false);
    const ACK_TIMEOUT_MS = 300;

    useEffect(() => {
        const isFinalState = state === "SUCCESS" || state === "ERROR";

        if (showAcknowledge && isFinalState) {
            // Push update to the next tick to avoid synchronous cascading renders
            const showTimer = setTimeout(() => setShowAck(true), 0);

            const hideTimer = setTimeout(() => {
                if (state !== "ERROR") {
                    setShowAck(false);
                }
            }, ACK_TIMEOUT_MS);

            return () => {
                clearTimeout(showTimer);
                clearTimeout(hideTimer);
            };
        }
    }, [state, showAcknowledge]);

    return showAck;
}
