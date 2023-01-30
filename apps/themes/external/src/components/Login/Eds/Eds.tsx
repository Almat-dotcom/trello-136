import Button from "components/parts/Button";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { useRef } from "react";

const Eds = ({ kcContext, i18n, hidden, onFormSubmit }: { kcContext: KcContext, i18n: I18n, hidden: boolean, onFormSubmit: () => void }) => {
    const { msgStr } = i18n

    const edsRef = useRef<HTMLInputElement>(null);

    return (
        <div className={hidden ? "hidden" : ""}>
            <input id="authType" name="authType" type="hidden" value={hidden ? "" : "eds"} />
            <input ref={edsRef} id="eds" name="eds" type="hidden" />
            <Button severity="primary" type="button" onClick={() => { }}>{msgStr("doSetCertificate")}</Button>
        </div>
    );
};

export default Eds;
