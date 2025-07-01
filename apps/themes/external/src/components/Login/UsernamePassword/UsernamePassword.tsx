import Button from "components/parts/Button";
import { InputField } from "components/parts/Input";
import { I18n } from "lib/i18n";
import { KcContextLogin } from "../Type";
import { useUsernamePasswordForm } from "./hooks";

type UsernameVariations = "iin" | "email";

const UsernamePassword = ({ kcContext, i18n, hidden, variant, onFormSubmit }: { kcContext: KcContextLogin, i18n: I18n, hidden: boolean, variant: UsernameVariations, onFormSubmit: () => void }) => {
    const { url, realm } = kcContext;
    const { msgStr, advancedMsgStr } = i18n;

    const { username, setUsername, password, setPassword, getError, onSubmit } = useUsernamePasswordForm(kcContext, variant, onFormSubmit);

    return (
        <div className={hidden ? "hidden" : ""}>
            <InputField
                fieldName="username"
                label={msgStr(variant === "email" ? "email" : "iin")}
                type="text"
                error={advancedMsgStr(getError("username") ?? "") ?? getError("username")}
                value={username}
                placeholder={variant === "iin" ? "- - - - - - - - - - - -" : " "}
                required
                onChange={(e) => { setUsername(e.target.value) }}
                onEnter={onSubmit} />
            <InputField
                fieldName="password"
                label={msgStr("password")}
                type="password"
                error={advancedMsgStr(getError("password") ?? "") ?? getError("password")}
                value={password}
                required
                onChange={(e) => { setPassword(e.target.value) }}
                onEnter={onSubmit} />

            <div className="flex items-center justify-between">
                {realm.rememberMe && (
                    <div className="flex flex-row items-center justify-center">
                        <div className="flex items-center h-5">
                            <input
                                id="rememberMe"
                                name="rememberMe"
                                type="checkbox"
                                className="bg-gray-50 border-gray-300 accent-primary  focus:ring-3 focus:ring-primary h-4 w-4 rounded"
                            />
                        </div>
                        <div className="text-sm ml-3">
                            <label htmlFor="rememberMe" className="font-medium text-gray-500">{msgStr("rememberMe")}</label>
                        </div>
                    </div>
                )}

                {realm.resetPasswordAllowed && (
                    <a
                        href={url.loginResetCredentialsUrl}
                        className="text-sm text-secondary-dark font-semibold underline"
                    >{msgStr("doForgotPassword")}</a>
                )}
            </div>

            <Button severity="primary" type="button" onClick={() => onSubmit()}>{msgStr("doLogIn")}</Button>
        </div>
    );
};

export default UsernamePassword;
