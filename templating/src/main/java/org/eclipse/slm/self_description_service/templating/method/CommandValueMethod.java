package org.eclipse.slm.self_description_service.templating.method;

import com.jayway.jsonpath.JsonPath;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.eclipse.slm.self_description_service.templating.utils.JsonPathReader;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


public class CommandValueMethod implements TemplateMethodModelEx {

    private final List<String> outputTypes = List.of("j", "x", "r");

    @Override
    public Object exec(List list) throws TemplateModelException {

        String command;
        Optional<String> outputType = Optional.empty();
        Optional<String> outputCommand = Optional.empty();


        if (list.size() == 1) {
            command = list.get(0).toString();
        } else if (list.size() == 3) {
            command = list.get(0).toString();
            outputType = Optional.of(list.get(1).toString());
            outputCommand = Optional.of(list.get(2).toString());

            if (!isOutputTypeValid(outputType.get())) {
                throw new TemplateModelException("Wrong output type parameter.");
            }

            if (outputCommand.get().isEmpty()) {
                throw new TemplateModelException("Wrong output command parameter.");
            }


        } else {
            throw new TemplateModelException("Wrong number of arguments");
        }

        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");

        ProcessBuilder processBuilder;
        if (isWindows) {
            processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        } else {
            processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
        }
        processBuilder.redirectErrorStream(true);
        Process p = null;
        try {
            p = processBuilder.start();

            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            String result = "";

            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                result = line;
            }

            if (outputType.isPresent()) {

                switch (outputType.get()) {
                    case "j":
                        var json = JsonPath.parse(result);
                        return JsonPathReader.readSingleValueFromPath(json, outputCommand.get());
                    case "x":

                        result = result.trim();
                        if (result.startsWith("\"") && result.endsWith("\"")) {
                            result = result.substring(1, result.length() - 1);
                        }

                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        var builder = dbf.newDocumentBuilder();
                        var xmlDocument = builder.parse(new InputSource(new StringReader(result)));
                        var xPath = XPathFactory.newInstance().newXPath();
                        var nodeList = (NodeList) xPath.compile(outputCommand.get()).evaluate(xmlDocument, XPathConstants.NODESET);
                        if (nodeList.getLength() > 0){
                            return nodeList.item(0).getTextContent();
                        }
                        return "";
                    case "r":
                        var pattern = Pattern.compile(outputCommand.get());
                        var matcher = pattern.matcher(result);
                        var match = matcher.results().findFirst();
                        if (match.isPresent()) {
                            return match.get().group(0);
                        }
                        return "";
                    default:
                        break;
                }

            }


            return result;
        } catch (IOException e) {
            return "";
        } catch (ParserConfigurationException e) {
            return "";
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean isOutputTypeValid(String type) {
        return this.outputTypes.contains(type);
    }


}
