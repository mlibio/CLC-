package org.clc.server.aspect;

import org.clc.server.annotation.MyApiResponse;
import org.clc.server.annotation.MyApiResponses;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * @version 1.0
 * @description: TODO
 */
@SupportedAnnotationTypes("org.clc.MyApiResponses")
public class MyApiResponseProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(MyApiResponses.class)) {
            TypeElement typeElement = (TypeElement) element;
            MyApiResponses apiResponses = typeElement.getAnnotation(MyApiResponses.class);
            generateApiResponses(typeElement, apiResponses);
        }
        return true;
    }

    private void generateApiResponses(TypeElement typeElement, MyApiResponses apiResponses) {
        try {
            PrintWriter writer = (PrintWriter) processingEnv.getFiler().createSourceFile(typeElement.getQualifiedName() + "Generated").openWriter();
            writer.println("package " + typeElement.getEnclosingElement().toString() + ";");
            writer.println("import io.swagger.v3.oas.annotations.responses.ApiResponse;");
            writer.println("import io.swagger.v3.oas.annotations.responses.ApiResponses;");
            writer.println("@ApiResponses({");
            for (MyApiResponse apiResponse : apiResponses.value()) {
                writer.println("    @ApiResponse(responseCode=\"" + apiResponse.responseCode() + "\", description=\"" + apiResponse.description() + "\"),");
            }
            writer.println("})");
            writer.close();
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate code: " + e.getMessage());
        }
    }
}
