import { memo, useState } from "react";
import Button from "components/parts/Button";
import { InputField } from "components/parts/Input";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { Layout } from "components/Layout";

const LoginConfigTotp = memo(({ kcContext, i18n }: { kcContext: KcContext; i18n: I18n; } & KcProps) => {
    const { totp, url, message } = kcContext;
    const { msgStr } = i18n;

    const [otp, setOtp] = useState("");

    const onSubmit = () => {
        const form = document.getElementById("totp-setup-form") as HTMLFormElement;
        form.submit();
    };

    return (
        <Layout kcContext={kcContext} i18n={i18n}>
            <div className="max-w-md mx-auto bg-white shadow-md rounded-lg p-6">
                <div className="text-center">
                    <h1 className="text-xl font-semibold text-gray-800">{msgStr("verifyTitle")}</h1>
                    <p className="mt-2 text-gray-600 text-sm">
                        {msgStr("verifyDescription")}
                    </p>
                </div>

                <form id="totp-setup-form" action={url.loginAction} method="post" className="mt-6 space-y-4">
                    <input type="hidden" name="totpSecret" value={totp.totpSecret} />

                    {message && (
                        <div className="bg-red-100 text-red-700 p-2 rounded-md">
                            {msgStr(message.summary)}
                        </div>
                    )}

                    <div className="text-center">
                        <p className="text-gray-700">{msgStr("downloadInstructions")}</p>

                        <img
                            src={`data:image/png;base64,${totp.totpSecretQrCode}`}
                            alt="TOTP QR Code"
                            className="mx-auto mt-4 w-40 h-40 border rounded-md"
                        />

                        <p className="text-gray-700 mt-4">{msgStr("scanCodeInstructions")}</p>
                        <div className="mt-2 bg-gray-100 text-gray-800 p-2 rounded-md font-mono text-sm">
                            {totp.totpSecretEncoded}
                        </div>
                    </div>

                    <div className="mt-4">
                        <InputField
                            fieldName="totp"
                            type="text"
                            label={msgStr("verificationCodeLabel")}
                            placeholder={msgStr("verificationCodePlaceholder")}
                            value={otp}
                            onChange={(e) => setOtp(e.target.value)}
                            required
                        />
                    </div>

                    <div className="flex justify-between items-center mt-6">
                        <Button severity="primary" type="submit" onClick={onSubmit}>
                            {msgStr("verifyButton")}
                        </Button>
                        {url.loginRestartFlowUrl && (
                            <Button severity="secondary" type="link" href={url.loginRestartFlowUrl}>
                                {msgStr("cancelButton")}
                            </Button>
                        )}
                    </div>
                </form>
            </div>
        </Layout>
    );
});

export default LoginConfigTotp;