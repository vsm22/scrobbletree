import React from "react";

class ToolDialog extends React.Component {

    render() {

        const ToolDialogComponent = this.props.toolDialogComponent;

        return (

            <div className="tool-dialog">
                <ToolDialogComponent {...this.props} />
            </div>
        );
    }
}

export default ToolDialog