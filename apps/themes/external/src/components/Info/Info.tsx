import { LayoutWithCarousel } from "components/Layout";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { memo } from "react";

type KcContext_Info = Extract<KcContext, { pageId: "info.ftl" }>;

const Info = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_Info, i18n: I18n } & KcProps) => {
    const { requiredActions, messageHeader, message, pageRedirectUri, actionUri, client } = kcContext;
    const { msg, advancedMsgStr } = i18n;

    const requiredActionsItems ="kskmd"
    return (
        <LayoutWithCarousel kcContext={kcContext} i18n={i18n}>
            <div>
                <div className="text-center">
                    {messageHeader && (
                        <p className="my-6 text-slate-900 text-2xl font-bold">{advancedMsgStr(messageHeader) ?? messageHeader}</p>
                    )}

                    {message?.summary && (
                        <p className="my-6 text-slate-900">{advancedMsgStr(message.summary) ?? message.summary}</p>
                    )}
                </div>

                <div className="mt-4 mb-48">
                    <>
                        {requiredActionsItems}
                        {(() => {
                            if (pageRedirectUri) {
                                return (
                                    <p className="text-gray-700"><a href={pageRedirectUri} className="text-md text-secondary-dark font-semibold underline">{msg("backToApplication")}</a></p>
                                );
                            } else if (actionUri) {
                                return (
                                    <p className="text-gray-700"><a href={actionUri} className="text-md text-secondary-dark font-semibold underline">{msg("proceedWithAction")}</a></p>
                                );
                            } else if (client?.baseUrl) {
                                return (
                                    <p className="text-gray-700"><a href={client.baseUrl} className="text-md text-secondary-dark font-semibold underline">{msg("backToApplication")}</a></p>
                                );
                            }
                        })()}
                    </>
                </div>
            </div>
        </LayoutWithCarousel>
    );
})

export default Info;
