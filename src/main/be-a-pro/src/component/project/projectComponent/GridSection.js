import styles from './GridSection.module.css';

function GridSection({children}) {
    return (
        <div className={styles.container}>
            <div className={styles.cardBox}>
                {children}
            </div>
        </div>
    )
}

export default GridSection;