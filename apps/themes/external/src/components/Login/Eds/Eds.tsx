import Button from "components/parts/Button";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { signAuthXml } from "lib/ncalayer";
import { CancelledByUser, ConnectionLost } from "lib/ncalayer/NCALayer";
import { useRef, useState } from "react";
import Alert from "../../parts/Alert";

const Eds = ({ kcContext, i18n, hidden, onFormSubmit }: { kcContext: KcContext, i18n: I18n, hidden: boolean, onFormSubmit: () => void }) => {
    const { client, realm } = kcContext;
    const token = client.clientId + realm.name;
    const { msgStr } = i18n

    const [error, setError] = useState('');

    const edsRef = useRef<HTMLInputElement>(null);

    const xml = `<Authentication><token>${token}</token></Authentication>`;
    const onClick = async () => {
        try {
            const result = await signAuthXml(xml);
            edsRef.current!.value = result;
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

    return (
        <div className={hidden ? "hidden" : ""}>
            {error && (
                <Alert i18n={i18n} type={error === 'ncaCancelled' ? 'warning' : 'error'} message={error}/>
            )}
            <input id="authType" name="authType" type="hidden" value={hidden ? "" : "eds"} />
            <input ref={edsRef} id="eds" name="eds" type="hidden" />
            <Button severity="primary" type="button" onClick={onClick}>{msgStr("doSetCertificate")}</Button>
        </div>
    );
};

export default Eds;
