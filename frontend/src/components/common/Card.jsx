import './Card.css';

export default function Card({ children, className = '', variant = 'default', ...props }) {
  return (
    <div className={`card card-${variant} ${className}`} {...props}>
      {children}
    </div>
  );
}
