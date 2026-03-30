/*
Copyright (c) 2026 Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/fxsvgimage
 */
package org.girod.javafx.svgimage.xml.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.girod.javafx.svgimage.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.SVGPath;

/**
 * A JavaFX Path that parses and renders SVG path data. Supports M, L, H, V, C, Q, S, T, A, Z commands (absolute and relative).
 *
 * @version 1.6
 */
public class SVGPathParser {
   /**
    * Enum representing the types of SVG path markers.
    */
   public enum MarkerType {
      /**
       * Marker at the start of the path.
       */
      START,
      /**
       * Markers at intermediate vertices.
       */
      MID,
      /**
       * Marker at the end of the path.
       */
      END
   }

   /**
    * Enum representing SVG path commands.
    */
   private enum CommandType {
      MOVETO('M', 2, ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT),
      LINETO('L', 2, ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT),
      HORIZONTAL_LINETO('H', 1, ParameterConverter.PARSE_LENGTH_WIDTH),
      VERTICAL_LINETO('V', 1, ParameterConverter.PARSE_LENGTH_HEIGHT),
      CUBIC_CURVETO('C', 6, ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT,
              ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT,
              ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT),
      SMOOTH_CUBIC_CURVETO('S', 4, ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT,
              ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT),
      QUADRATIC_CURVETO('Q', 4, ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT,
              ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT),
      SMOOTH_QUADRATIC_CURVETO('T', 2, ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT,
              ParameterConverter.PARSE_NOT, ParameterConverter.PARSE_LENGTH_HEIGHT),
      ARC('A', 7, ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT,
              ParameterConverter.PARSE_DOUBLE_PROTECTED, ParameterConverter.PARSE_NOT, ParameterConverter.PARSE_NOT,
              ParameterConverter.PARSE_LENGTH_WIDTH, ParameterConverter.PARSE_LENGTH_HEIGHT),
      CLOSEPATH('Z', 0);

      private final char symbol;
      private final int paramCount;
      private final ParameterConverter[] numberConverters;

      private static final Map<Character, CommandType> SYMBOL_TO_COMMAND = Stream.of(values())
              .collect(Collectors.toMap(type -> type.symbol, Function.identity()));

      CommandType(char symbol, int paramCount, ParameterConverter... converters) {
         this.symbol = symbol;
         this.paramCount = paramCount;
         this.numberConverters = converters;

         /*
         Removed because the assert lead to exceptions in some valid real-world use cases
         */
         //assert this.numberConverters.length != paramCount : "The paramcount should be the same as the length of the number converter array";
      }

      private char getSymbol() {
         return symbol;
      }

      private int getParamCount() {
         return paramCount;
      }

      private static CommandType fromSymbol(char symbol) {
         CommandType type = SYMBOL_TO_COMMAND.get(Character.toUpperCase(symbol));
         if (type == null) {
            throw new IllegalArgumentException("Unknown command: " + symbol);
         }
         return type;
      }

      ParameterConverter getParameterConverter(int index) {
         return numberConverters[index];
      }
   }

   private enum ParameterConverter {
      PARSE_NOT,
      PARSE_LENGTH_WIDTH,
      PARSE_LENGTH_HEIGHT,
      PARSE_DOUBLE_PROTECTED;
   }

   private static final Pattern COMMAND_PATTERN = Pattern.compile("([MLHVCSQTAZmlhvcsqtaz])([^MLHVCSQTAZmlhvcsqtaz]*)",
           Pattern.CASE_INSENSITIVE);
   private static final Pattern NUMBER_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?([a-zA-Z%]+)?",
           Pattern.CASE_INSENSITIVE);

   private final List<Point2D> markerList = new ArrayList<>();
   private List<PathCommand> commandList = new ArrayList<>();
   private String content;

   /**
    * Create a path parser.
    */
   public SVGPathParser() {
   }
   
   /**
    * Parse a path content.
    *
    * @param content the path content
    * @param viewport the viewport
    * @return the path taking into account the viewport and the units
    */
   public List<SVGPath> parsePathContent(String content, Viewport viewport) {
      List<SVGPath> listPath = new ArrayList<>();
      parse(content, viewport);
      SVGPath path = new SVGPath();

      path.getProperties().put("PathParser", this);
      path.setContent(getContent());
      listPath.add(path);
      return listPath;
   }

   /**
    * Gets the content string after the length has been resolved and the viewport has been taken into account.
    *
    * @return The SVG path data string
    */
   public String getContent() {
      if (content == null) {
         content = commandList.stream().map(PathCommand::getContent).collect(Collectors.joining(" "));
      }
      return content;
   }

   /**
    * Return the list of markers.
    *
    * @return the list of markers
    */
   public List<Point2D> getMarkerList() {
      return markerList;
   }

   /**
    * Returns the coordinates for the specified marker type. - START: First MoveTo point - MID: All intermediate vertices (endpoints of segments) -
    * END: Last point of the path
    *
    * @param type The MarkerType to retrieve coordinates for
    * @return List of Point2D coordinates for the specified marker type
    */
   public List<Point2D> getMarkerList(MarkerType type) {
      switch (type) {
         case START:
            return markerList.size() < 1 ? Collections.emptyList()
                    : new ArrayList<>(Collections.singletonList(markerList.get(0)));
         case MID:
            return markerList.size() < 3 ? Collections.emptyList()
                    : new ArrayList<>(markerList.subList(1, markerList.size() - 1));
         case END:
            return markerList.size() < 2 ? Collections.emptyList()
                    : new ArrayList<>(Collections.singletonList(markerList.get(markerList.size() - 1)));
         default:
            return Collections.emptyList();
      }
   }

   /**
    * Updates the internal Path and calculates marker coordinates from the SVG path data.
    *
    * @param content the path content
    * @param viewport the viewport
    */
   public void parse(String content, Viewport viewport) {
      markerList.clear();

      if (content == null || content.trim().isEmpty()) {
         return;
      }

      commandList = parse(viewport, content);
      double currentX = 0, currentY = 0;

      for (PathCommand cmd : commandList) {
         double[] params = cmd.getParameters();
         boolean isRelative = cmd.isRelative();
         PathElement element;
         double x, y;

         switch (cmd.getType()) {
            case MOVETO:
               x = isRelative ? currentX + params[0] : params[0];
               y = isRelative ? currentY + params[1] : params[1];
               element = new MoveTo(x, y);
               currentX = x;
               currentY = y;
               break;
            case LINETO:
               x = isRelative ? currentX + params[0] : params[0];
               y = isRelative ? currentY + params[1] : params[1];
               element = new LineTo(x, y);
               currentX = x;
               currentY = y;
               break;
            case HORIZONTAL_LINETO:
               x = isRelative ? currentX + params[0] : params[0];
               y = currentY;
               element = new LineTo(x, y);
               currentX = x;
               break;
            case VERTICAL_LINETO:
               x = currentX;
               y = isRelative ? currentY + params[0] : params[0];
               element = new LineTo(x, y);
               currentY = y;
               break;
            case CUBIC_CURVETO:
               x = isRelative ? currentX + params[4] : params[4];
               y = isRelative ? currentY + params[5] : params[5];
               element = new CubicCurveTo(isRelative ? currentX + params[0] : params[0],
                       isRelative ? currentY + params[1] : params[1], isRelative ? currentX + params[2] : params[2],
                       isRelative ? currentY + params[3] : params[3], x, y);
               currentX = x;
               currentY = y;
               break;
            case SMOOTH_CUBIC_CURVETO:
               x = isRelative ? currentX + params[2] : params[2];
               y = isRelative ? currentY + params[3] : params[3];
               element = new CubicCurveTo(currentX, currentY, isRelative ? currentX + params[0] : params[0],
                       isRelative ? currentY + params[1] : params[1], x, y);
               currentX = x;
               currentY = y;
               break;
            case QUADRATIC_CURVETO:
               x = isRelative ? currentX + params[2] : params[2];
               y = isRelative ? currentY + params[3] : params[3];
               element = new QuadCurveTo(isRelative ? currentX + params[0] : params[0],
                       isRelative ? currentY + params[1] : params[1], x, y);
               currentX = x;
               currentY = y;
               break;
            case SMOOTH_QUADRATIC_CURVETO:
               x = isRelative ? currentX + params[0] : params[0];
               y = isRelative ? currentY + params[1] : params[1];
               element = new QuadCurveTo(currentX, currentY, x, y);
               currentX = x;
               currentY = y;
               break;
            case ARC:
               x = isRelative ? currentX + params[5] : params[5];
               y = isRelative ? currentY + params[6] : params[6];
               element = new ArcTo(params[0], params[1], params[2], x, y, params[3] == 1, params[4] == 1);
               currentX = x;
               currentY = y;
               break;
            case CLOSEPATH:
               element = new ClosePath();
               x = markerList.get(0).getX();
               y = markerList.get(0).getX();
               break;
            default:
               throw new IllegalArgumentException("Unknown command: " + cmd.getType());
         }
         markerList.add(new Point2D(x, y));
      }
   }

   /**
    * Internal class to represent a parsed SVG path command.
    */
   private static class PathCommand {
      private final String id;
      private final CommandType type;
      private final double[] parameters;
      private final boolean isRelative;

      public PathCommand(char id, CommandType type, double[] parameters, boolean isRelative) {
         this.id = Character.toString(id);
         this.type = type;
         this.parameters = parameters;
         this.isRelative = isRelative;
      }

      public CommandType getType() {
         return type;
      }

      public double[] getParameters() {
         return parameters;
      }

      public boolean isRelative() {
         return isRelative;
      }

      public String getContent() {
         StringBuilder builder;

         builder = new StringBuilder("");
         builder.append(id);
         for (int i = 0; i < type.paramCount; i++) {
            double parameter = parameters[i];
            builder.append(" ");
            builder.append(type.getParameterConverter(i) == ParameterConverter.PARSE_NOT ? (int) parameter : Double.toString(parameter));
         }

         return builder.toString();
      }
   }

   /**
    * Parses an SVG path string into a list of PathCommand objects.
    *
    * @param viewport
    *
    * @param pathData The SVG path data string
    * @return List of parsed PathCommand objects
    * @throws IllegalArgumentException if the path data is invalid
    */
   private List<PathCommand> parse(Viewport viewport, String pathData) {
      List<PathCommand> commands = new ArrayList<>();
      Matcher commandMatcher = COMMAND_PATTERN.matcher(pathData.trim());

      while (commandMatcher.find()) {
         String command = commandMatcher.group(1);
         String params = commandMatcher.group(2).trim();
         char cmdChar = command.charAt(0);
         boolean isRelative = Character.isLowerCase(cmdChar);
         CommandType cmd = CommandType.fromSymbol(cmdChar);

         double[] parameters = parseParameters(cmdChar, cmd, viewport, params, cmd.getParamCount());
         int paramCount = cmd.getParamCount();

         if (paramCount == 0 || parameters.length == paramCount) {
            commands.add(new PathCommand(cmdChar, cmd, parameters, isRelative));
         } else {
            // Split repeated parameter groups into separate commands.
            double[] firstGroup = new double[paramCount];
            System.arraycopy(parameters, 0, firstGroup, 0, paramCount);
            commands.add(new PathCommand(cmdChar, cmd, firstGroup, isRelative));

            // Per SVG spec, extra coordinate pairs after M/m become implicit L/l commands.
            // For all other commands, extra groups repeat the same command.
            CommandType repeatCmd = cmd;
            char repeatChar = cmdChar;
            if (cmd == CommandType.MOVETO) {
               repeatCmd = CommandType.LINETO;
               repeatChar = isRelative ? 'l' : 'L';
            }

            for (int offset = paramCount; offset < parameters.length; offset += paramCount) {
               double[] group = new double[paramCount];
               System.arraycopy(parameters, offset, group, 0, paramCount);
               commands.add(new PathCommand(repeatChar, repeatCmd, group, isRelative));
            }
         }
      }

      return commands;
   }

   /**
    * Parses a string of parameters into a flat double array containing all parameter sets, handling optional units.
    */
   private double[] parseParameters(char cmdChar, CommandType commandType, Viewport viewport, String params, int expectedCount) {
      List<String> numbers = new ArrayList<>();
      Matcher numberMatcher = NUMBER_PATTERN.matcher(params);
      while (numberMatcher.find()) {
         numbers.add(numberMatcher.group());
      }

      if (expectedCount == 0) {
         if (!numbers.isEmpty()) {
            throw new IllegalArgumentException("Unexpected parameters for command "
                    + cmdChar + ": got " + numbers.size() + ", expected 0");
         }
         return new double[0];
      }

      if (numbers.isEmpty()) {
         throw new IllegalArgumentException("Missing parameters for command "
                 + cmdChar + ": got 0, expected " + expectedCount);
      }

      if (numbers.size() % expectedCount != 0) {
         throw new IllegalArgumentException("Invalid number of parameters for command "
                 + cmdChar + ": got " + numbers.size()
                 + ", expected a multiple of " + expectedCount);
      }

      int countOfSets = numbers.size() / expectedCount;
      if (countOfSets < 1) {
         throw new IllegalArgumentException("Invalid number of parameters for command, expected multiple of " + expectedCount);
      }

      return IntStream.range(0, countOfSets)
              .mapToObj(setIndex -> parseParametersSet(commandType, viewport, setIndex, expectedCount, numbers))
              .flatMapToDouble(java.util.Arrays::stream)
              .toArray();
   }

   /**
    * Parses a single set of parameters from a list of number strings, applying the appropriate
    * unit conversions based on the command type.
    *
    * @param commandType   the SVG path command type, used to determine how each parameter is converted
    * @param viewport      the current viewport, used for converting length values with units
    * @param setIndex      the index of the parameter set within the overall list of parameters
    * @param expectedCount the expected number of parameters in a single set for this command
    * @param numbers       the complete list of parameter strings extracted from the path data
    * @return an array of doubles representing the converted parameters for the specified set
    */
   private double[] parseParametersSet(CommandType commandType, Viewport viewport, int setIndex, int expectedCount, List<String> numbers) {
      double dpi = viewport.getDPI();
      return IntStream.range(0, expectedCount).mapToDouble(indexInSet -> {
         int numberIndex = setIndex * expectedCount + indexInSet;
         String number = numbers.get(numberIndex);
         switch (commandType.getParameterConverter(indexInSet)) {
            case PARSE_DOUBLE_PROTECTED:
               return ParserUtils.parseDoubleSizeProtected(dpi, number);
            case PARSE_LENGTH_HEIGHT:
               return LengthParser.parseLength(number, false, viewport);
            case PARSE_LENGTH_WIDTH:
               return LengthParser.parseLength(number, true, viewport);
            case PARSE_NOT:
               return Double.parseDouble(number);
            default:
               return 0D;
         }
      }).toArray();
   }

}
