import React, { memo, useState, useRef } from "react";
import type { KcProps } from "keycloakify";
import type { I18n } from "lib/i18n";
import Button from "components/parts/Button";
import { KcContext } from "lib/kc";
import { Layout } from "components/Layout";

type KcContextExtendedOTPForm = Extract<KcContext, { pageId: "login-otp.ftl" }>;

const LoginOtp = memo(({ kcContext, i18n }: { kcContext: KcContextExtendedOTPForm; i18n: I18n; } & KcProps) => {
    const { url } = kcContext;
    const { msgStr } = i18n;

    const [otp, setOtp] = useState<string[]>(new Array(6).fill(""));
    const inputsRef = useRef<HTMLInputElement[]>([]);

    const handleChange = (value: string, index: number) => {
        const newOtp = [...otp];
        newOtp[index] = value.slice(-1);
        setOtp(newOtp);

        if (value && index < 5) {
            inputsRef.current[index + 1]?.focus();
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
        if (e.key === "Backspace" && !otp[index] && index > 0) {
            inputsRef.current[index - 1]?.focus();
        }
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        const otpCode = otp.join("");
        console.log("Submitted OTP:", otpCode);
        const form = document.getElementById("otp-form") as HTMLFormElement;
        form.submit();
    };

    return (
        <Layout kcContext={kcContext} i18n={i18n}>
            <main className="flex justify-center items-center min-h-screen ">
                <div className=" bg-white shadow-2xl rounded-lg p-20 border border-gray-500">
                    {/* Заголовок и описание */}
                    <div className="text-center mb-10">
                        <h1 className="text-4xl font-extrabold text-gray-800 mb-6">{msgStr("enterOtp")}</h1>
                        <p className="text-lg text-gray-600">{msgStr("setupTotp")}</p>
                    </div>

                    <form
                        id="otp-form"
                        method="post"
                        action={url.loginAction}
                        onSubmit={handleSubmit}
                        className="space-y-8"
                    >
                        {/* Поля для ввода OTP */}
                        <div className="flex justify-center space-x-4">
                            {otp.map((digit, index) => (
                                <input
                                    key={index}
                                    ref={(el) => (inputsRef.current[index] = el!)}
                                    type="text"
                                    maxLength={1}
                                    value={digit}
                                    onChange={(e) => handleChange(e.target.value, index)}
                                    onKeyDown={(e) => handleKeyDown(e, index)}
                                    className="w-16 h-16 text-center text-2xl font-bold border border-gray-300 rounded-lg focus:outline-none focus:ring-4 focus:ring-blue-500 transition-all shadow-sm"
                                />
                            ))}
                        </div>

                        {/* Кнопка подтверждения */}
                        <div className="text-center">
                            <Button severity="primary" type="submit">
                                {msgStr("confirm")}
                            </Button>
                        </div>
                    </form>
                </div>
            </main>
        </Layout>
    );
});

export default LoginOtp;
