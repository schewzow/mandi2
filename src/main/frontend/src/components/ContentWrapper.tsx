import * as React from "react";

interface Props
{
   children: React.ReactNode;
   style?: React.CSSProperties;
}

class ContentWrapper extends React.Component<Props>
{
   shouldComponentUpdate(nextProps: Props)
   {
      return this.props.children !== nextProps.children;
   }

   render()
   {
      return (
         <div style={this.props.style}>
            {this.props.children}
         </div>
      );
   }
}

export default ContentWrapper;

