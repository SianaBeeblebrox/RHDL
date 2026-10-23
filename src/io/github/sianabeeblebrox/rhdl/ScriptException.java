package io.github.sianabeeblebrox.rhdl;

/**
 *
 */
public final class ScriptException extends RuntimeException {
    private RHDLParser.Location location = null;

    public ScriptException(final String message, final Object... args) {
        super(String.format(message, args));
    }

    @Deprecated
    public ScriptException() {}

    void setLocation(final RHDLParser.Location location) {
        if(this.location == null) {
            this.location = location;
        }
    }

    @Override
    public String getMessage() {
        return super.getMessage() + (this.location == null ? "" : String.format(" (@%s)", this.location));
    }
}
