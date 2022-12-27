import Lang from "components/parts/Lang";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { ReactComponent as Logo } from './logo.svg';

const Layout = ({ children, kcContext, i18n, ...props }: { children: any, kcContext: KcContext, i18n: I18n }) => {
    const { locale } = kcContext
    const { currentLanguageTag, supported } = locale ?? { currentLanguageTag: "en", supported: [] }
    return (
        <div className="bg-gradient-to-tr from-back-dark to-back-light">
            <div className="flex justify-between items-stretch min-h-screen p-2">
                <div className="hidden lg:block">
                    <Logo />
                </div>

                <div className="flex flex-col items-center lg:block bg-white w-full lg:w-1/2 xl:w-2/5 shadow-none lg:shadow-lg mt-0 sm:m-4 lg:mt-16 px-6 md:px-24 pt-4 lg:pt-10 pb-6">
                    <div className="flex justify-between w-full lg:hidden mb-4">
                        <Logo />
                        <Lang current={currentLanguageTag} langs={supported} i18n={i18n} />
                    </div>
                    <div className="w-full sm:w-2/3 lg:w-full">{children}</div>
                </div>


                <div className="hidden lg:flex lg:flex-col items-end p-2">
                    <Lang current={currentLanguageTag} langs={supported} i18n={i18n} />
                </div>
            </div>
        </div>
    );
};

export default Layout;
