import { memo, useState, useRef } from "react";
import Button from "components/parts/Button";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { Layout } from "components/Layout";

type KcContext_LoginConfigTotp = Extract<KcContext, { pageId: "login-config-totp.ftl" }>;

const LoginConfigTotp = memo(({ kcContext, i18n }: { kcContext: KcContext_LoginConfigTotp; i18n: I18n; } & KcProps) => {
    const { totp, url, message } = kcContext;
    const { msgStr } = i18n;

    const [otp, setOtp] = useState<string[]>(new Array(6).fill(""));
    const inputsRef = useRef<HTMLInputElement[]>([]);

    const handleChange = (value: string, index: number) => {
        const newOtp = [...otp];
        newOtp[index] = value.slice(-1); // Берём только последнюю цифру
        setOtp(newOtp);

        if (value && index < 5) {
            inputsRef.current[index + 1]?.focus(); // Автоматический переход к следующему полю
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
        if (e.key === "Backspace" && !otp[index] && index > 0) {
            inputsRef.current[index - 1]?.focus(); // Возврат к предыдущему полю
        }
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        const otpCode = otp.join("");
        console.log("Submitted OTP:", otpCode);
        // Отправляем форму
        const form = document.getElementById("totp-setup-form") as HTMLFormElement;
        form.submit();
    };

    return (
        <Layout kcContext={kcContext} i18n={i18n}>
            <div className="max-w-lg mx-auto bg-white shadow-lg rounded-lg p-6 border border-gray-200">
                <div className="text-center">
                    <h1 className="text-2xl font-bold text-gray-800 mb-4">{msgStr("verifyTitle")}</h1>
                    <p className="text-sm text-gray-600 mb-6">{msgStr("verifyDescription")}</p>
                </div>

                <form
                    id="totp-setup-form"
                    action={url.loginAction}
                    method="post"
                    onSubmit={handleSubmit}
                    className="space-y-6"
                >
                    <input type="hidden" name="totpSecret" value={totp.totpSecret} />

                    {message && (
                        <div className="bg-red-50 text-red-700 p-4 rounded-lg border border-red-300 text-sm">
                            {message.summary}
                        </div>
                    )}

                    <div className="text-center">
                        <p className="text-gray-700">{msgStr("downloadInstructions")}</p>
                        <img
                            src={`data:image/png;base64,${totp.totpSecretQrCode}`}
                            alt="TOTP QR Code"
                            className="mx-auto mt-4 w-40 h-40 border border-gray-300 rounded-md"
                        />
                        <p className="text-gray-700 mt-4">{msgStr("scanCodeInstructions")}</p>
                        <div className="mt-2 bg-gray-100 text-gray-900 p-3 rounded-md font-mono text-sm">
                            {totp.totpSecretEncoded}
                        </div>
                    </div>

                    {/* Поля для ввода 6-значного кода */}
                    <div className="flex justify-center space-x-2">
                        {otp.map((digit, index) => (
                            <input
                                key={index}
                                ref={(el) => (inputsRef.current[index] = el!)}
                                type="text"
                                maxLength={1}
                                value={digit}
                                onChange={(e) => handleChange(e.target.value, index)}
                                onKeyDown={(e) => handleKeyDown(e, index)}
                                className="w-12 h-12 text-center text-lg font-semibold border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all shadow-sm"
                            />
                        ))}
                    </div>

                    {/* Кнопки подтверждения и отмены */}
                    <div className="flex justify-between items-center mt-6">
                        <Button
                            severity="primary"
                            type="submit"
                        >
                            {msgStr("verifyButton")}
                        </Button>
                        {url.loginRestartFlowUrl && (
                            <Button
                                severity="secondary"
                                type="link"
                                href={url.loginRestartFlowUrl}
                            >
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
