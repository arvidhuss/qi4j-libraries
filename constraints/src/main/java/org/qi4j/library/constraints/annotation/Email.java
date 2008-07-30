package org.qi4j.library.constraints.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.qi4j.composite.ConstraintDeclaration;

@ConstraintDeclaration
@Retention( RetentionPolicy.RUNTIME )
@NotEmpty
@Contains( "@" )
public @interface Email
{
}