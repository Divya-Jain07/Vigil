import './Button.css';

export default function Button({ 
  children, 
  variant = 'primary', 
  fullWidth = false, 
  icon: Icon,
  className = '',
  ...props 
}) {
  const classes = `btn btn-${variant} ${fullWidth ? 'btn-full' : ''} ${className}`;
  
  return (
    <button className={classes} {...props}>
      {children}
      {Icon && <Icon size={18} className="btn-icon" />}
    </button>
  );
}
