import styles from './SearchBar.module.scss';

export default function SearchBar({children}) {
    return (
        <div className={styles.containerOfSearch}>
            {children}
        </div>
    )
}