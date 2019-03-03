package br.com.alexandre.kale.docx;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.JAXBElement;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class ImageReplacer implements Closeable {

    private static Logger logger = LoggerFactory.getLogger(ImageReplacer.class);
    
    private AtomicInteger number;
    private WordprocessingMLPackage doc;
    private File destination;
    
    public ImageReplacer(final File source, final File destination) {
        this.number = new AtomicInteger(7000);
        try {
            this.doc = WordprocessingMLPackage.load(source);
            this.destination = destination;
        } catch (Docx4JException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void replace(final String placeholder, final byte[] image) {
        try {
            replace(doc, placeholder, image);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void replace(final WordprocessingMLPackage doc, final String placeholder, byte[] image) {
        try {
            final List<Object> paragraphs = getAllElementFromObject(doc.getMainDocumentPart(), P.class);
            for(final Object par : paragraphs) {
                final P p = (P) par;
                final List<Object> texts = getAllElementFromObject(p, Text.class);
                for(final Object text : texts) {
                    final Inline inline = createInline(doc, image);
                    final Text t = (Text) text;
                    final String value = Strings.nullToEmpty(t.getValue());
                    logger.info("Value: '{}'", value);
                    if(value.contains(placeholder)) {
                        logger.debug("Processing text... '{}'", placeholder);
                        final R oldObject = (R) t.getParent();
                        final List<Object> newObjects = new ArrayList<>();
                        final String imagePlaceholder = "::image";
                        final List<String> parts = getTextParts(t.getValue(), imagePlaceholder);
                        logger.info("Parts: '{}'", parts);
                        for (final String part: parts) {
                            if (!part.equals(imagePlaceholder)) {
                                logger.debug("Creating text: '{}'", part);
                                newObjects.add(createText(part, oldObject.getRPr()));
                            } else {
                                logger.debug("Creating inline image...");
                                newObjects.add(createInlineImage(inline));
                            }
                        }
                        logger.debug("Replacing elements...");
                        p.replaceElement(oldObject, newObjects);
                        break;
                    } 
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private R createText(final String value, final RPr pr) {
        final ObjectFactory factory = new ObjectFactory();

        final R run = factory.createR();
        run.setRPr(pr);

        final Text text = factory.createText();
        text.setValue(value);
        text.setSpace("preserve");

        run.getContent().add(text);
        return run;
    }

    private R createInlineImage(final Inline inline) {
        final ObjectFactory factory = new ObjectFactory();

        final R run = factory.createR();

        final Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);

        drawing.getAnchorOrInline().add(inline);
        return run;
    }

    private Inline createInline(final WordprocessingMLPackage doc, byte[] image) throws Exception {
        final BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(doc, image);
        final int docPrId = number.incrementAndGet();
        final int cNvPrId = number.incrementAndGet();
        return imagePart.createImageInline("Image " + docPrId, "Image " + cNvPrId, docPrId, cNvPrId, false);
    }

    private List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        final List<Object> result = new ArrayList<Object>();
        if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();

        if (obj.getClass().equals(toSearch)) {
            result.add(obj);
        } else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementFromObject(child, toSearch));
            }
        }
        return result;
    }

    private List<String> getTextParts(final String text, final String imagePlaceholder) {
        String replaceAll = text.replaceAll("\\$\\{\\w+\\}", imagePlaceholder);
        final List<String> parts = new ArrayList<>();        
        while (replaceAll.indexOf(imagePlaceholder) >= 0) {
            final String part = replaceAll.substring(0, replaceAll.indexOf(imagePlaceholder));
            if (!Strings.isNullOrEmpty(part)) {
                parts.add(part);
            }
            parts.add(imagePlaceholder);
            replaceAll = replaceAll.substring(replaceAll.indexOf(imagePlaceholder) + imagePlaceholder.length());
        }
        if (!Strings.isNullOrEmpty(replaceAll)) {
            parts.add(replaceAll);
        }
        return parts;
    }

    @Override
    public void close() throws IOException {
        try {
            doc.save(destination);
        } catch (Docx4JException e) {
            throw new IOException(e);
        }        
    }
}