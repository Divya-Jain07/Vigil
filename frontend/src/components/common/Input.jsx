import './Input.css';

export default function Input({ icon: Icon, className = '', ...props }) {
  return (
    <div className={`input-wrapper ${className}`}>
      {Icon && <Icon size={18} className="input-icon" />}
      <input className={`input-field ${Icon ? 'with-icon' : ''}`} {...props} />
    </div>
  );
}
