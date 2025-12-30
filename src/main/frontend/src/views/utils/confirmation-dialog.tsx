import '../../assets/css/modal.css'
//import {useCallback, useEffect} from "react";

export const ConfirmationDialog: React.FC<{
    yesButtonName: string,
    bodyText: string,
    onCloseButtonClick: (confirmed: boolean) => void,
}> = (props) => {

    // useEscapeKey(props.onCloseButtonClick);
    //
    // const KEY_NAME_ESC = 'Escape';
    // const KEY_EVENT_TYPE = 'keyup';
    //
    // function useEscapeKey(handleClose: (confirmed: boolean) => void) {
    //     const handleEscKey = useCallback((event: KeyboardEvent) => {
    //         if (event.key === KEY_NAME_ESC) {
    //             handleClose(false);
    //         }
    //     }, [handleClose]);
    //
    //     useEffect(() => {
    //         document.addEventListener(KEY_EVENT_TYPE, handleEscKey, false);
    //
    //         return () => {
    //             document.removeEventListener(KEY_EVENT_TYPE, handleEscKey, false);
    //         };
    //     }, [handleEscKey]);
    // }

    return (
        <div className={'modal display-block'}>
            <section className="modal-main rounded" style={{width: '40%'}}>
                <div className={'container'}>
                    <div className="modal-header">
                        <h4 className="modal-title">Confirmation</h4>

                        <button type="button" className="btn-close"
                                data-bs-dismiss="modal" aria-label="Close"
                                onClick={() => props.onCloseButtonClick(false)}>
                        </button>
                    </div>
                    <div className="modal-body">
                        <pre>{props.bodyText}</pre>
                    </div>
                    <div className="modal-footer">
                        <button type="button" className={'btn btn btn-sm btn-secondary'} onClick={() => {
                            props.onCloseButtonClick(false);
                        }}> Close
                        </button>
                        <button type="button" className={'btn btn btn-sm btn-primary'} onClick={() => {
                            props.onCloseButtonClick(true);
                        }}>
                            {props.yesButtonName}
                        </button>
                    </div>
                </div>
            </section>
        </div>
    );
};
