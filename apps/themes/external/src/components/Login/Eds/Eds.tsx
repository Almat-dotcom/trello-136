import Button from "components/parts/Button";
import { I18n } from "lib/i18n";
import { signAuthXml } from "lib/ncalayer";
import { CancelledByUser, ConnectionLost } from "lib/ncalayer/NCALayer";
import { useRef, useState } from "react";
import Alert from "../../parts/Alert";

const Eds = ({ i18n, hidden, onFormSubmit }: { i18n: I18n, hidden: boolean, onFormSubmit: () => void }) => {
    const { msgStr } = i18n

    const [error, setError] = useState('');

    const edsRef = useRef<HTMLInputElement>(null);

    const xml = `<Authentication><token>${Date.now()}</token></Authentication>`;
    const onClick = async () => {
        try {
            setError('ncaSignProgress');
            const result = await signAuthXml(xml);
            edsRef.current!.value = result;
            setError('ncaSignFinished');
            onFormSubmit();
        } catch (error) {
            console.error(error);
            if (error === ConnectionLost) {
                setError('ncaConnectionLost');
            } else if (error === CancelledByUser) {
                setError('ncaCancelled');
            } else {
                setError('ncaFailed');
            }
        }
    }

    let type: "success" | "warning" | "error" | "info" = 'error';
    if (error === 'ncaSignProgress') {
        type = 'info';
    }
    if (error === 'ncaSignFinished') {
        type = 'success';
    }
    if (error === 'ncaCancelled') {
        type = 'warning'
    }
    return (
        <div className={hidden ? "hidden" : ""}>
            {error && (
                <Alert i18n={i18n} type={type} message={error} />
            )}
            <input id="authType" name="authType" type="hidden" value={hidden ? "" : "eds"} />
            <input ref={edsRef} id="eds" name="eds" type="hidden" />
            <Button severity="primary" type="button" disabled={error === 'ncaSignProgress' || error === 'ncaSignFinished'} onClick={onClick}>{msgStr("doSetCertificate")}</Button>
        </div>
    );
};

export default Eds;
