import styles from './Banner.module.css';
import { Swiper, SwiperSlide } from 'swiper/react';
import SwiperCore, { Navigation, Pagination, Autoplay } from "swiper";
import { useEffect } from 'react';
import 'swiper/scss'
import 'swiper/scss/navigation'
import 'swiper/scss/pagination'
import banner from '../images/banner/banner.png';

SwiperCore.use([Navigation, Pagination, Autoplay])


function Banner() {

    useEffect(() => {
        const prevBtn = document.querySelector(".swiper-button-prev");
        prevBtn.style.marginLeft = "360px";
        prevBtn.style.color = "#A9A9A9";
        const nextBtn = document.querySelector(".swiper-button-next");
        nextBtn.style.marginRight = "360px";
        nextBtn.style.color = "#A9A9A9";

        var activePage = document.querySelector(".swiper-pagination-bullet-active");
        activePage.style.backgroundColor = "white";
        activePage.style.width = "20px"
        activePage.style.borderRadius = "20px 20px";
        activePage.style.transition = "all 0.5s";
    })

    return (
        <div className={styles.banner}>

        <Swiper
            className={styles.swiper}
            spaceBetween={50}
            slidesPerView={1}
            navigation
            pagination={{ clickable: true }}
            autoplay={{ 
                delay: 3000,
                disableOnInteraction: false,
            }}
            onSlideChange={(e) => {
                for (let i = 0; i<e.pagination.bullets.length; i++) {
                    if (e.pagination.bullets[i].classList.contains("swiper-pagination-bullet-active")) {
                        e.pagination.bullets[i].style.backgroundColor = "white";
                        e.pagination.bullets[i].style.width = "20px";
                        e.pagination.bullets[i].style.borderRadius = "20px 20px";
                        e.pagination.bullets[i].style.transition = "all 0.5s";
                    }
                    else {
                        e.pagination.bullets[i].style.width = "8px";
                        e.pagination.bullets[i].style.borderRadius = "50%";
                        e.pagination.bullets[i].style.transition = "all 0.5s";
                    }
                };
            }}
            >

            <SwiperSlide>
                <img src={banner} className={styles.imageOfBanner} alt="첫번째 이미지"/>
            </SwiperSlide>
            <SwiperSlide>
                <img src={banner} className={styles.imageOfBanner} alt="두번째 이미지"/>
            </SwiperSlide>
            <SwiperSlide>
                <img src={banner} className={styles.imageOfBanner} alt="세번째 이미지"/>
            </SwiperSlide>
            <SwiperSlide>
                <img src={banner} className={styles.imageOfBanner} alt="네번째 이미지"/>
            </SwiperSlide>
        </Swiper>
        </div>
    )
}

export default Banner;