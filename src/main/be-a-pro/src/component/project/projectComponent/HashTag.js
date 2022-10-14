import styles from './HashTag.module.css';

export default function HashTag(props) {
    return (
        <div className={styles.hashTag}>
            <span className={styles.textOfTag}>
                {props.text}
            </span>
        </div>
    )
}