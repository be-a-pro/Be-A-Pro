import styles from './Header.module.css';
import logo from '../images/be-a-pro-b.svg';
import search from '../images/search-icon.svg';
import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import $ from 'jquery';
import jquery from 'jquery';
import { useRef } from 'react';
import Signup from './Signup';

function Header() {

    const modal = useRef(null);
    const [state, setState] = useState(false);

    function checkSignUp() {
        setState(true);
    }

    useEffect(() => {
        if (state) {
            modal.current.style.display = 'block';
            document.body.style.overflow = "hidden";
        } else {
            modal.current.style.display = 'none';
            document.body.style.overflow = "auto";
        }
    }, [state])

    return (
        <header className={styles.header}>
            {/* 여기에 모달창이 삽입될거예요 :-) */}
            <div className={styles.modal} ref={modal}>
                <Signup state={state} setState={setState}/>
            </div>

            <nav className={styles.nav}>
                <span className={styles.logo}>
                <Link to='/'>
                    <img src={logo} alt="로고 이미지"></img>
                </Link>
                </span>
                <span className={styles.header_project}>
                    <Link to='/projectlist' >PROJECT</Link>
                </span>
                <span className={styles.header_pro}>
                    <Link to='#'>PRO</Link>
                </span>
                <span className={styles.header_community}>
                    <Link to='#' >COMMUNITY</Link>
                </span>
                <div className={styles.search_container}>
                    <input type="search" className={styles.search_bar} placeholder='NFT에 대해 알 수 있는 프로젝트는?'></input>
                    <img src={search} className={styles.search_icon} alt="검색 아이콘"/>
                </div>
                <span className={styles.header_login}>
                    <Link to='#' onClick={checkSignUp}>LOGIN</Link>
                </span>
            </nav>
        </header>
         )
}

export default Header;