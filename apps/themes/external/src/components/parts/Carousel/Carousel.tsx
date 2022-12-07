import { Carousel as PrimeCarousel } from "primereact/carousel";
import News from "./news.jpg";
import News2 from "./news_2.jpg";
import News3 from "./news_3.jpg";
import ArrowBack from "./arrow_back.svg";
import ArrowForward from "./arrow_forward.svg";
import { useState } from "react";

const Carousel = () => {
    const [ page, setPage ] = useState(0)

    const next = () => {
        if (page === (data.length - 1)) {
            setPage(0);
        } else {
            setPage(page + 1);
        }
    }

    const prev = () => {
        if (page === 0) {
            setPage(data.length - 1);
        } else {
            setPage(page - 1);
        }
    }

    const productTemplate = (article: any) => {
        return (
            <div>
                <div className="flex flex-col items-center">
                    <div className="m-3">
                        <img
                            src={article.img}
                            alt={article.name}
                            className="rounded-md" />
                    </div>
                    <div>
                        <span>{article.text}</span>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="m-1 w-4/5 xl:w-2/3 text-center py-8">
            <div className="flex items-center justify-center">
                <button className="ml-4 p-2 rounded-full transition-colors hover:bg-orange-100" onClick={() => prev()}>
                    <img className="ml-0.5" src={ArrowBack} alt="arrow_back" />
                </button>
                <PrimeCarousel value={data} numVisible={1} numScroll={1} page={page} showNavigators={false}
                    itemTemplate={productTemplate} header={""} />
                <button className="mr-4 p-2 rounded-full transition-colors hover:bg-orange-100" onClick={() => next()}>
                    <img src={ArrowForward} alt="arrow_back" />
                </button>
            </div>
        </div>
    );
}

export default Carousel;

const data = [{
    name: "news-1",
    img: News,
    text: "Утверждены поправки в План закупок товаров, работ и услуг Центрального депозитария на 2022 год"
}, {
    name: "news-2",
    img: News2,
    text: "Результаты заочных голосований членов Совета директоров Центрального депозитария"
}, {
    name: "news-3",
    img: News3,
    text: "Обновлены сведения об аукционах краткосрочных нот Национального Банка РК за последние 6 месяцев"
}]
