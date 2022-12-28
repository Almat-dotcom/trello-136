import { LayoutWithCarousel } from "components/Layout";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { memo } from "react";

type KcContext_Error = Extract<KcContext, { pageId: "error.ftl" }>;

const Error = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_Error, i18n: I18n } & KcProps) => {
    const { message, client } = kcContext;
    const { msg, msgStr, advancedMsgStr } = i18n;

    return (
        <LayoutWithCarousel kcContext={kcContext} i18n={i18n}>
            <div>
                <div className="text-center">
                    <p className="my-6 text-red-900 text-3xl font-bold">{msgStr("errorTitle")}</p>
                </div>

                <div className="my-6">
                    <p className="text-slate-900">{advancedMsgStr(message.summary) ?? message.summary}</p>
                </div>

                <div className="mt-4 mb-48">
                    {(() => {
                        if (client?.baseUrl) {
                            return (
                                <p className="text-gray-700"><a href={client.baseUrl} className="text-secondary-dark font-semibold underline">{msg("backToApplication")}</a></p>
                            );
                        }
                    })()}
                </div>
            </div>
        </LayoutWithCarousel>
    );
})

export default Error;
