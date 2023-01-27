import Button from "components/parts/Button";
import { InputSelect } from "components/parts/Input";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { useRef, useState } from "react";

const Eds = ({ kcContext, i18n, hidden, onFormSubmit }: { kcContext: KcContext, i18n: I18n, hidden: boolean, onFormSubmit: () => void }) => {
    const { messagesPerField } = kcContext
    const { msgStr } = i18n

    const extractError = (name: string) => {
        try {
            if (messagesPerField.exists(name) && messagesPerField.existsError(name)) {
                return messagesPerField.get(name);
            }
        } catch (error) {
            return undefined;
        }
        return undefined;
    }

    const [edsType, setEdsType] = useState("")

    const edsRef = useRef<HTMLInputElement>(null)

    return (
        <div className={hidden ? "hidden" : ""}>
            <input id="authType" name="authType" type="hidden" value={hidden ? "" : "eds"} />
            <input ref={edsRef} id="eds" name="eds" type="hidden"/>
            <InputSelect
                fieldName="edsType"
                label="Выберите тип хранилища ЭЦП"
                options={[{ value: "PKC12", label: "Ваш компьютер" }]}
                value={edsType}
                required
                error={extractError("eds")}
                onValueChange={(option) => setEdsType(option.value)}
            />
            <Button severity="primary" type="button" onClick={() => {}}>{msgStr("doLogIn")}</Button>
        </div>
    );
};

export default Eds;
