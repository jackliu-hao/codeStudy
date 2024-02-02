package io.undertow.client.http;

import io.undertow.util.BadRequestException;
import io.undertow.util.HttpString;
import java.nio.ByteBuffer;
import java.util.Map;

public class HttpResponseParser$$generated extends HttpResponseParser {
   private static final byte[] STATE_BYTES_407;
   private static final byte[] STATE_BYTES_405;
   private static final HttpString HTTP_STRING_318;
   private static final byte[] STATE_BYTES_409;
   private static final HttpString HTTP_STRING_439;
   private static final HttpString HTTP_STRING_205;
   private static final HttpString HTTP_STRING_326;
   private static final HttpString HTTP_STRING_447;
   private static final HttpString HTTP_STRING_204;
   private static final HttpString HTTP_STRING_324;
   private static final HttpString HTTP_STRING_445;
   private static final HttpString HTTP_STRING_202;
   private static final byte[] STATE_BYTES_403;
   private static final HttpString HTTP_STRING_322;
   private static final HttpString HTTP_STRING_443;
   private static final HttpString HTTP_STRING_200;
   private static final byte[] STATE_BYTES_401;
   private static final HttpString HTTP_STRING_320;
   private static final HttpString HTTP_STRING_441;
   private static final byte[] STATE_BYTES_418;
   private static final byte[] STATE_BYTES_416;
   private static final HttpString HTTP_STRING_308;
   private static final HttpString HTTP_STRING_429;
   private static final HttpString HTTP_STRING_306;
   private static final HttpString HTTP_STRING_427;
   private static final HttpString HTTP_STRING_316;
   private static final HttpString HTTP_STRING_437;
   private static final HttpString HTTP_STRING_314;
   private static final HttpString HTTP_STRING_435;
   private static final HttpString HTTP_STRING_312;
   private static final byte[] STATE_BYTES_414;
   private static final HttpString HTTP_STRING_433;
   private static final HttpString HTTP_STRING_310;
   private static final HttpString HTTP_STRING_431;
   private static final HttpString HTTP_STRING_419;
   private static final HttpString HTTP_STRING_417;
   private static final HttpString HTTP_STRING_305;
   private static final HttpString HTTP_STRING_304;
   private static final HttpString HTTP_STRING_425;
   private static final HttpString HTTP_STRING_302;
   private static final HttpString HTTP_STRING_423;
   private static final byte[] STATE_BYTES_502;
   private static final HttpString HTTP_STRING_300;
   private static final HttpString HTTP_STRING_421;
   private static final byte[] STATE_BYTES_500;
   private static final HttpString HTTP_STRING_408;
   private static final HttpString HTTP_STRING_406;
   private static final HttpString HTTP_STRING_415;
   private static final HttpString HTTP_STRING_413;
   private static final HttpString HTTP_STRING_412;
   private static final HttpString HTTP_STRING_411;
   private static final HttpString HTTP_STRING_410;
   private static final byte[] STATE_BYTES_208;
   private static final byte[] STATE_BYTES_329;
   private static final byte[] STATE_BYTES_206;
   private static final byte[] STATE_BYTES_327;
   private static final byte[] STATE_BYTES_448;
   private static final byte[] STATE_BYTES_201;
   private static final HttpString HTTP_STRING_404;
   private static final byte[] STATE_BYTES_321;
   private static final byte[] STATE_BYTES_442;
   private static final HttpString HTTP_STRING_402;
   private static final byte[] STATE_BYTES_440;
   private static final HttpString HTTP_STRING_400;
   private static final byte[] STATE_BYTES_325;
   private static final byte[] STATE_BYTES_446;
   private static final byte[] STATE_BYTES_203;
   private static final byte[] STATE_BYTES_323;
   private static final byte[] STATE_BYTES_444;
   private static final byte[] STATE_BYTES_218;
   private static final byte[] STATE_BYTES_339;
   private static final byte[] STATE_BYTES_459;
   private static final byte[] STATE_BYTES_212;
   private static final byte[] STATE_BYTES_333;
   private static final byte[] STATE_BYTES_453;
   private static final byte[] STATE_BYTES_210;
   private static final byte[] STATE_BYTES_331;
   private static final byte[] STATE_BYTES_451;
   private static final byte[] STATE_BYTES_216;
   private static final byte[] STATE_BYTES_337;
   private static final byte[] STATE_BYTES_457;
   private static final byte[] STATE_BYTES_214;
   private static final byte[] STATE_BYTES_455;
   private static final byte[] STATE_BYTES_307;
   private static final byte[] STATE_BYTES_428;
   private static final byte[] STATE_BYTES_426;
   private static final byte[] STATE_BYTES_309;
   private static final HttpString HTTP_STRING_503;
   private static final byte[] STATE_BYTES_420;
   private static final HttpString HTTP_STRING_501;
   private static final byte[] STATE_BYTES_303;
   private static final byte[] STATE_BYTES_424;
   private static final byte[] STATE_BYTES_301;
   private static final byte[] STATE_BYTES_422;
   private static final byte[] STATE_BYTES_319;
   private static final byte[] STATE_BYTES_317;
   private static final byte[] STATE_BYTES_438;
   private static final byte[] STATE_BYTES_311;
   private static final byte[] STATE_BYTES_432;
   private static final byte[] STATE_BYTES_430;
   private static final byte[] STATE_BYTES_315;
   private static final byte[] STATE_BYTES_436;
   private static final byte[] STATE_BYTES_313;
   private static final byte[] STATE_BYTES_434;
   private static final HttpString HTTP_STRING_286;
   private static final HttpString HTTP_STRING_164;
   private static final HttpString HTTP_STRING_284;
   private static final HttpString HTTP_STRING_162;
   private static final byte[] STATE_BYTES_120;
   private static final byte[] STATE_BYTES_241;
   private static final HttpString HTTP_STRING_282;
   private static final HttpString HTTP_STRING_160;
   private static final byte[] STATE_BYTES_361;
   private static final byte[] STATE_BYTES_482;
   private static final byte[] STATE_BYTES_71;
   private static final HttpString HTTP_STRING_280;
   private static final byte[] STATE_BYTES_480;
   private static final byte[] STATE_BYTES_73;
   private static final byte[] STATE_BYTES_124;
   private static final byte[] STATE_BYTES_245;
   private static final byte[] STATE_BYTES_365;
   private static final byte[] STATE_BYTES_486;
   private static final byte[] STATE_BYTES_122;
   private static final byte[] STATE_BYTES_243;
   private static final byte[] STATE_BYTES_363;
   private static final byte[] STATE_BYTES_484;
   private static final byte[] STATE_BYTES_77;
   private static final byte[] STATE_BYTES_128;
   private static final byte[] STATE_BYTES_249;
   private static final HttpString HTTP_STRING_168;
   private static final byte[] STATE_BYTES_369;
   private static final byte[] STATE_BYTES_79;
   private static final byte[] STATE_BYTES_126;
   private static final byte[] STATE_BYTES_247;
   private static final HttpString HTTP_STRING_288;
   private static final HttpString HTTP_STRING_166;
   private static final byte[] STATE_BYTES_367;
   private static final byte[] STATE_BYTES_488;
   private static final byte[] STATE_BYTES_59;
   private static final HttpString HTTP_STRING_154;
   private static final HttpString HTTP_STRING_396;
   private static final byte[] STATE_BYTES_490;
   private static final HttpString HTTP_STRING_274;
   private static final HttpString HTTP_STRING_152;
   private static final HttpString HTTP_STRING_394;
   private static final HttpString HTTP_STRING_272;
   private static final HttpString HTTP_STRING_150;
   private static final byte[] STATE_BYTES_373;
   private static final HttpString HTTP_STRING_392;
   private static final byte[] STATE_BYTES_494;
   private static final byte[] STATE_BYTES_130;
   private static final byte[] STATE_BYTES_251;
   private static final HttpString HTTP_STRING_270;
   private static final byte[] STATE_BYTES_371;
   private static final HttpString HTTP_STRING_390;
   private static final byte[] STATE_BYTES_492;
   private static final byte[] STATE_BYTES_61;
   private static final byte[] STATE_BYTES_135;
   private static final byte[] STATE_BYTES_377;
   private static final byte[] STATE_BYTES_498;
   private static final byte[] STATE_BYTES_63;
   private static final byte[] STATE_BYTES_255;
   private static final byte[] STATE_BYTES_375;
   private static final byte[] STATE_BYTES_496;
   private static final byte[] STATE_BYTES_65;
   private static final byte[] STATE_BYTES_132;
   private static final byte[] STATE_BYTES_253;
   private static final byte[] STATE_BYTES_139;
   private static final HttpString HTTP_STRING_158;
   private static final byte[] STATE_BYTES_67;
   private static final byte[] STATE_BYTES_259;
   private static final HttpString HTTP_STRING_278;
   private static final byte[] STATE_BYTES_137;
   private static final HttpString HTTP_STRING_156;
   private static final byte[] STATE_BYTES_379;
   private static final HttpString HTTP_STRING_398;
   private static final byte[] STATE_BYTES_69;
   private static final byte[] STATE_BYTES_257;
   private static final HttpString HTTP_STRING_276;
   private static final byte[] STATE_BYTES_108;
   private static final byte[] STATE_BYTES_229;
   private static final byte[] STATE_BYTES_349;
   private static final HttpString HTTP_STRING_264;
   private static final HttpString HTTP_STRING_142;
   private static final HttpString HTTP_STRING_263;
   private static final HttpString HTTP_STRING_384;
   private static final HttpString HTTP_STRING_262;
   private static final HttpString HTTP_STRING_140;
   private static final HttpString HTTP_STRING_382;
   private static final HttpString HTTP_STRING_260;
   private static final HttpString HTTP_STRING_380;
   private static final byte[] STATE_BYTES_102;
   private static final byte[] STATE_BYTES_96;
   private static final byte[] STATE_BYTES_222;
   private static final byte[] STATE_BYTES_343;
   private static final byte[] STATE_BYTES_464;
   private static final byte[] STATE_BYTES_100;
   private static final byte[] STATE_BYTES_98;
   private static final HttpString HTTP_STRING_148;
   private static final byte[] STATE_BYTES_220;
   private static final byte[] STATE_BYTES_341;
   private static final byte[] STATE_BYTES_462;
   private static final byte[] STATE_BYTES_106;
   private static final byte[] STATE_BYTES_227;
   private static final HttpString HTTP_STRING_268;
   private static final HttpString HTTP_STRING_146;
   private static final byte[] STATE_BYTES_347;
   private static final HttpString HTTP_STRING_388;
   private static final byte[] STATE_BYTES_468;
   private static final byte[] STATE_BYTES_104;
   private static final byte[] STATE_BYTES_225;
   private static final HttpString HTTP_STRING_266;
   private static final HttpString HTTP_STRING_144;
   private static final byte[] STATE_BYTES_345;
   private static final HttpString HTTP_STRING_386;
   private static final byte[] STATE_BYTES_466;
   private static final byte[] STATE_BYTES_118;
   private static final byte[] STATE_BYTES_239;
   private static final HttpString HTTP_STRING_374;
   private static final HttpString HTTP_STRING_495;
   private static final HttpString HTTP_STRING_131;
   private static final HttpString HTTP_STRING_252;
   private static final HttpString HTTP_STRING_372;
   private static final HttpString HTTP_STRING_493;
   private static final HttpString HTTP_STRING_250;
   private static final byte[] STATE_BYTES_351;
   private static final HttpString HTTP_STRING_370;
   private static final byte[] STATE_BYTES_472;
   private static final HttpString HTTP_STRING_491;
   private static final byte[] STATE_BYTES_81;
   private static final byte[] STATE_BYTES_470;
   private static final byte[] STATE_BYTES_83;
   private static final byte[] STATE_BYTES_355;
   private static final byte[] STATE_BYTES_476;
   private static final byte[] STATE_BYTES_85;
   private static final byte[] STATE_BYTES_112;
   private static final byte[] STATE_BYTES_233;
   private static final HttpString HTTP_STRING_138;
   private static final byte[] STATE_BYTES_353;
   private static final byte[] STATE_BYTES_474;
   private static final byte[] STATE_BYTES_87;
   private static final byte[] STATE_BYTES_110;
   private static final byte[] STATE_BYTES_231;
   private static final HttpString HTTP_STRING_258;
   private static final HttpString HTTP_STRING_136;
   private static final byte[] STATE_BYTES_359;
   private static final HttpString HTTP_STRING_378;
   private static final HttpString HTTP_STRING_499;
   private static final byte[] STATE_BYTES_89;
   private static final byte[] STATE_BYTES_116;
   private static final byte[] STATE_BYTES_237;
   private static final HttpString HTTP_STRING_256;
   private static final HttpString HTTP_STRING_134;
   private static final byte[] STATE_BYTES_357;
   private static final HttpString HTTP_STRING_376;
   private static final byte[] STATE_BYTES_478;
   private static final HttpString HTTP_STRING_497;
   private static final byte[] STATE_BYTES_114;
   private static final HttpString HTTP_STRING_133;
   private static final byte[] STATE_BYTES_235;
   private static final HttpString HTTP_STRING_254;
   private static final byte[] STATE_BYTES_26;
   private static final byte[] STATE_BYTES_28;
   private static final HttpString HTTP_STRING_119;
   private static final HttpString HTTP_STRING_121;
   private static final HttpString HTTP_STRING_242;
   private static final byte[] STATE_BYTES_281;
   private static final HttpString HTTP_STRING_362;
   private static final HttpString HTTP_STRING_483;
   private static final HttpString HTTP_STRING_240;
   private static final HttpString HTTP_STRING_360;
   private static final HttpString HTTP_STRING_481;
   private static final byte[] STATE_BYTES_285;
   private static final byte[] STATE_BYTES_163;
   private static final byte[] STATE_BYTES_283;
   private static final byte[] STATE_BYTES_161;
   private static final HttpString HTTP_STRING_129;
   private static final byte[] STATE_BYTES_289;
   private static final byte[] STATE_BYTES_30;
   private static final byte[] STATE_BYTES_167;
   private static final HttpString HTTP_STRING_127;
   private static final HttpString HTTP_STRING_248;
   private static final byte[] STATE_BYTES_287;
   private static final byte[] STATE_BYTES_32;
   private static final byte[] STATE_BYTES_165;
   private static final HttpString HTTP_STRING_368;
   private static final HttpString HTTP_STRING_489;
   private static final HttpString HTTP_STRING_125;
   private static final HttpString HTTP_STRING_246;
   private static final byte[] STATE_BYTES_34;
   private static final HttpString HTTP_STRING_366;
   private static final HttpString HTTP_STRING_487;
   private static final HttpString HTTP_STRING_123;
   private static final HttpString HTTP_STRING_244;
   private static final byte[] STATE_BYTES_36;
   private static final byte[] STATE_BYTES_169;
   private static final HttpString HTTP_STRING_364;
   private static final HttpString HTTP_STRING_485;
   private static final byte[] STATE_BYTES_16;
   private static final byte[] STATE_BYTES_18;
   private static final HttpString HTTP_STRING_109;
   private static final byte[] STATE_BYTES_171;
   private static final HttpString HTTP_STRING_352;
   private static final HttpString HTTP_STRING_473;
   private static final HttpString HTTP_STRING_230;
   private static final byte[] STATE_BYTES_291;
   private static final HttpString HTTP_STRING_350;
   private static final HttpString HTTP_STRING_471;
   private static final byte[] STATE_BYTES_175;
   private static final byte[] STATE_BYTES_295;
   private static final byte[] STATE_BYTES_173;
   private static final byte[] STATE_BYTES_293;
   private static final byte[] STATE_BYTES_179;
   private static final HttpString HTTP_STRING_117;
   private static final HttpString HTTP_STRING_238;
   private static final byte[] STATE_BYTES_299;
   private static final byte[] STATE_BYTES_177;
   private static final HttpString HTTP_STRING_358;
   private static final HttpString HTTP_STRING_479;
   private static final HttpString HTTP_STRING_115;
   private static final HttpString HTTP_STRING_236;
   private static final byte[] STATE_BYTES_297;
   private static final byte[] STATE_BYTES_22;
   private static final HttpString HTTP_STRING_356;
   private static final HttpString HTTP_STRING_477;
   private static final HttpString HTTP_STRING_113;
   private static final HttpString HTTP_STRING_234;
   private static final byte[] STATE_BYTES_24;
   private static final HttpString HTTP_STRING_354;
   private static final HttpString HTTP_STRING_475;
   private static final HttpString HTTP_STRING_111;
   private static final HttpString HTTP_STRING_232;
   private static final byte[] STATE_BYTES_48;
   private static final HttpString HTTP_STRING_219;
   private static final HttpString HTTP_STRING_340;
   private static final HttpString HTTP_STRING_461;
   private static final HttpString HTTP_STRING_460;
   private static final byte[] STATE_BYTES_141;
   private static final byte[] STATE_BYTES_383;
   private static final byte[] STATE_BYTES_261;
   private static final byte[] STATE_BYTES_381;
   private static final byte[] STATE_BYTES_51;
   private static final HttpString HTTP_STRING_107;
   private static final HttpString HTTP_STRING_228;
   private static final byte[] STATE_BYTES_267;
   private static final byte[] STATE_BYTES_145;
   private static final HttpString HTTP_STRING_348;
   private static final byte[] STATE_BYTES_387;
   private static final HttpString HTTP_STRING_469;
   private static final byte[] STATE_BYTES_53;
   private static final HttpString HTTP_STRING_105;
   private static final HttpString HTTP_STRING_226;
   private static final byte[] STATE_BYTES_265;
   private static final byte[] STATE_BYTES_143;
   private static final HttpString HTTP_STRING_346;
   private static final byte[] STATE_BYTES_385;
   private static final HttpString HTTP_STRING_467;
   private static final byte[] STATE_BYTES_55;
   private static final HttpString HTTP_STRING_103;
   private static final HttpString HTTP_STRING_224;
   private static final byte[] STATE_BYTES_149;
   private static final HttpString HTTP_STRING_223;
   private static final HttpString HTTP_STRING_344;
   private static final HttpString HTTP_STRING_465;
   private static final byte[] STATE_BYTES_57;
   private static final HttpString HTTP_STRING_101;
   private static final byte[] STATE_BYTES_269;
   private static final byte[] STATE_BYTES_147;
   private static final HttpString HTTP_STRING_221;
   private static final HttpString HTTP_STRING_342;
   private static final byte[] STATE_BYTES_389;
   private static final HttpString HTTP_STRING_463;
   private static final byte[] STATE_BYTES_38;
   private static final HttpString HTTP_STRING_209;
   private static final HttpString HTTP_STRING_207;
   private static final HttpString HTTP_STRING_328;
   private static final HttpString HTTP_STRING_449;
   private static final HttpString HTTP_STRING_330;
   private static final byte[] STATE_BYTES_391;
   private static final HttpString HTTP_STRING_450;
   private static final byte[] STATE_BYTES_153;
   private static final byte[] STATE_BYTES_395;
   private static final byte[] STATE_BYTES_273;
   private static final byte[] STATE_BYTES_151;
   private static final byte[] STATE_BYTES_393;
   private static final byte[] STATE_BYTES_271;
   private static final byte[] STATE_BYTES_40;
   private static final byte[] STATE_BYTES_157;
   private static final HttpString HTTP_STRING_217;
   private static final HttpString HTTP_STRING_338;
   private static final byte[] STATE_BYTES_399;
   private static final byte[] STATE_BYTES_277;
   private static final HttpString HTTP_STRING_458;
   private static final byte[] STATE_BYTES_42;
   private static final byte[] STATE_BYTES_155;
   private static final HttpString HTTP_STRING_215;
   private static final HttpString HTTP_STRING_336;
   private static final byte[] STATE_BYTES_397;
   private static final byte[] STATE_BYTES_275;
   private static final HttpString HTTP_STRING_335;
   private static final HttpString HTTP_STRING_456;
   private static final byte[] STATE_BYTES_44;
   private static final HttpString HTTP_STRING_213;
   private static final HttpString HTTP_STRING_334;
   private static final HttpString HTTP_STRING_454;
   private static final byte[] STATE_BYTES_46;
   private static final byte[] STATE_BYTES_159;
   private static final HttpString HTTP_STRING_211;
   private static final HttpString HTTP_STRING_332;
   private static final byte[] STATE_BYTES_279;
   private static final HttpString HTTP_STRING_452;
   private static final HttpString HTTP_STRING_60;
   private static final HttpString HTTP_STRING_66;
   private static final HttpString HTTP_STRING_68;
   private static final HttpString HTTP_STRING_62;
   private static final HttpString HTTP_STRING_64;
   private static final HttpString HTTP_STRING_70;
   private static final HttpString HTTP_STRING_76;
   private static final HttpString HTTP_STRING_78;
   private static final HttpString HTTP_STRING_72;
   private static final HttpString HTTP_STRING_74;
   private static final HttpString HTTP_STRING_75;
   private static final HttpString HTTP_STRING_9;
   private static final HttpString HTTP_STRING_5;
   private static final HttpString HTTP_STRING_6;
   private static final HttpString HTTP_STRING_7;
   private static final HttpString HTTP_STRING_3;
   private static final HttpString HTTP_STRING_4;
   private static final HttpString HTTP_STRING_80;
   private static final HttpString HTTP_STRING_82;
   private static final HttpString HTTP_STRING_88;
   private static final byte[] STATE_BYTES_181;
   private static final HttpString HTTP_STRING_84;
   private static final byte[] STATE_BYTES_185;
   private static final HttpString HTTP_STRING_86;
   private static final byte[] STATE_BYTES_183;
   private static final byte[] STATE_BYTES_189;
   private static final byte[] STATE_BYTES_10;
   private static final byte[] STATE_BYTES_187;
   private static final byte[] STATE_BYTES_12;
   private static final HttpString HTTP_STRING_90;
   private static final HttpString HTTP_STRING_91;
   private static final HttpString HTTP_STRING_92;
   private static final HttpString HTTP_STRING_93;
   private static final byte[] STATE_BYTES_193;
   private static final HttpString HTTP_STRING_99;
   private static final byte[] STATE_BYTES_191;
   private static final HttpString HTTP_STRING_94;
   private static final byte[] STATE_BYTES_197;
   private static final HttpString HTTP_STRING_95;
   private static final byte[] STATE_BYTES_195;
   private static final HttpString HTTP_STRING_97;
   private static final byte[] STATE_BYTES_199;
   private static final HttpString HTTP_STRING_190;
   private static final HttpString HTTP_STRING_198;
   private static final HttpString HTTP_STRING_196;
   private static final HttpString HTTP_STRING_194;
   private static final HttpString HTTP_STRING_192;
   private static final HttpString HTTP_STRING_186;
   private static final HttpString HTTP_STRING_184;
   private static final HttpString HTTP_STRING_182;
   private static final HttpString HTTP_STRING_180;
   private static final HttpString HTTP_STRING_188;
   private static final HttpString HTTP_STRING_176;
   private static final HttpString HTTP_STRING_296;
   private static final HttpString HTTP_STRING_174;
   private static final HttpString HTTP_STRING_294;
   private static final HttpString HTTP_STRING_172;
   private static final HttpString HTTP_STRING_292;
   private static final HttpString HTTP_STRING_170;
   private static final HttpString HTTP_STRING_290;
   private static final HttpString HTTP_STRING_178;
   private static final HttpString HTTP_STRING_298;
   private static final byte[] STATE_BYTES_8;
   private static final HttpString HTTP_STRING_11;
   private static final HttpString HTTP_STRING_13;
   private static final HttpString HTTP_STRING_19;
   private static final HttpString HTTP_STRING_14;
   private static final HttpString HTTP_STRING_15;
   private static final HttpString HTTP_STRING_17;
   private static final HttpString HTTP_STRING_21;
   private static final HttpString HTTP_STRING_23;
   private static final HttpString HTTP_STRING_29;
   private static final HttpString HTTP_STRING_25;
   private static final HttpString HTTP_STRING_27;
   private static final HttpString HTTP_STRING_33;
   private static final HttpString HTTP_STRING_35;
   private static final HttpString HTTP_STRING_31;
   private static final HttpString HTTP_STRING_37;
   private static final HttpString HTTP_STRING_39;
   private static final HttpString HTTP_STRING_43;
   private static final HttpString HTTP_STRING_45;
   private static final HttpString HTTP_STRING_41;
   private static final HttpString HTTP_STRING_47;
   private static final HttpString HTTP_STRING_49;
   private static final HttpString HTTP_STRING_54;
   private static final HttpString HTTP_STRING_56;
   private static final HttpString HTTP_STRING_50;
   private static final HttpString HTTP_STRING_52;
   private static final HttpString HTTP_STRING_58;

   protected final void handleHeader(ByteBuffer var1, ResponseParseState var2, HttpResponseBuilder var3) throws BadRequestException {
      boolean var10;
      if (!var1.hasRemaining()) {
         var10 = false;
      } else {
         int var4;
         int var5;
         HttpString var6;
         byte[] var8;
         label871: {
            StringBuilder var7;
            label849: {
               byte var10000;
               label782: {
                  label805: {
                     label806: {
                        label807: {
                           label808: {
                              label809: {
                                 label810: {
                                    label811: {
                                       label812: {
                                          label813: {
                                             label814: {
                                                label815: {
                                                   label816: {
                                                      label817: {
                                                         label872: {
                                                            label819: {
                                                               label820: {
                                                                  label821: {
                                                                     label822: {
                                                                        label823: {
                                                                           label824: {
                                                                              label825: {
                                                                                 label826: {
                                                                                    label827: {
                                                                                       label757: {
                                                                                          var7 = var2.stringBuilder;
                                                                                          if ((var4 = var2.parseState) != 0) {
                                                                                             var5 = var2.pos;
                                                                                             var6 = var2.current;
                                                                                             var8 = var2.currentBytes;
                                                                                             switch (var4) {
                                                                                                case -2:
                                                                                                   break label805;
                                                                                                case -1:
                                                                                                   break label782;
                                                                                                case 0:
                                                                                                   break;
                                                                                                case 1:
                                                                                                   break label815;
                                                                                                case 2:
                                                                                                   break label757;
                                                                                                case 3:
                                                                                                   break label827;
                                                                                                case 4:
                                                                                                   break label826;
                                                                                                case 5:
                                                                                                   break label825;
                                                                                                case 6:
                                                                                                   break label824;
                                                                                                case 7:
                                                                                                   break label823;
                                                                                                case 8:
                                                                                                   break label821;
                                                                                                case 9:
                                                                                                   break label817;
                                                                                                case 10:
                                                                                                   break label807;
                                                                                                case 11:
                                                                                                   break label814;
                                                                                                case 12:
                                                                                                   break label811;
                                                                                                case 13:
                                                                                                   break label816;
                                                                                                case 14:
                                                                                                   break label806;
                                                                                                case 15:
                                                                                                   break label819;
                                                                                                case 16:
                                                                                                   break label813;
                                                                                                case 17:
                                                                                                   break label820;
                                                                                                case 18:
                                                                                                   break label809;
                                                                                                case 19:
                                                                                                   break label822;
                                                                                                case 20:
                                                                                                   break label872;
                                                                                                case 21:
                                                                                                   break label812;
                                                                                                case 22:
                                                                                                   break label810;
                                                                                                case 23:
                                                                                                   break label808;
                                                                                                default:
                                                                                                   throw new RuntimeException("Invalid character");
                                                                                             }
                                                                                          } else {
                                                                                             var5 = 0;
                                                                                             var6 = null;
                                                                                             var8 = null;
                                                                                          }

                                                                                          while(true) {
                                                                                             var10000 = var2.leftOver;
                                                                                             if (var10000 == 0) {
                                                                                                if (!var1.hasRemaining()) {
                                                                                                   break label871;
                                                                                                }

                                                                                                var10000 = var1.get();
                                                                                             } else {
                                                                                                var2.leftOver = 0;
                                                                                             }

                                                                                             if (var10000 == 65) {
                                                                                                var4 = 1;
                                                                                                break label815;
                                                                                             }

                                                                                             if (var10000 == 67) {
                                                                                                var4 = 2;
                                                                                                break;
                                                                                             }

                                                                                             if (var10000 == 68) {
                                                                                                var4 = -2;
                                                                                                var6 = HTTP_STRING_198;
                                                                                                var8 = STATE_BYTES_197;
                                                                                                var5 = 1;
                                                                                                break label805;
                                                                                             }

                                                                                             if (var10000 == 69) {
                                                                                                var4 = 11;
                                                                                                break label814;
                                                                                             }

                                                                                             if (var10000 == 76) {
                                                                                                var4 = 12;
                                                                                                break label811;
                                                                                             }

                                                                                             if (var10000 == 80) {
                                                                                                var4 = 13;
                                                                                                break label816;
                                                                                             }

                                                                                             if (var10000 == 82) {
                                                                                                var4 = 15;
                                                                                                break label819;
                                                                                             }

                                                                                             if (var10000 == 83) {
                                                                                                var4 = 17;
                                                                                                break label820;
                                                                                             }

                                                                                             if (var10000 == 84) {
                                                                                                var4 = 19;
                                                                                                break label822;
                                                                                             }

                                                                                             if (var10000 == 86) {
                                                                                                var4 = 22;
                                                                                                break label810;
                                                                                             }

                                                                                             if (var10000 == 87) {
                                                                                                var4 = 23;
                                                                                                break label808;
                                                                                             }

                                                                                             if (var10000 != 58 && var10000 != 13 && var10000 != 10 && var10000 != 32) {
                                                                                                var4 = -1;
                                                                                                var7.append("").append((char)var10000);
                                                                                                break label782;
                                                                                             }

                                                                                             if (var10000 == 10) {
                                                                                                var2.parseComplete();
                                                                                                var10 = false;
                                                                                                return;
                                                                                             }

                                                                                             if (!var1.hasRemaining()) {
                                                                                                break label871;
                                                                                             }
                                                                                          }
                                                                                       }

                                                                                       if (!var1.hasRemaining()) {
                                                                                          break label871;
                                                                                       }

                                                                                       var10000 = var1.get();
                                                                                       if (var10000 == 97) {
                                                                                          var4 = -2;
                                                                                          var6 = HTTP_STRING_52;
                                                                                          var8 = STATE_BYTES_51;
                                                                                          var5 = 2;
                                                                                          break label805;
                                                                                       }

                                                                                       if (var10000 != 111) {
                                                                                          if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                                                             var2.nextHeader = HTTP_STRING_50;
                                                                                             var2.state = 5;
                                                                                             break label849;
                                                                                          }

                                                                                          var4 = -1;
                                                                                          var7.append("C").append((char)var10000);
                                                                                          break label782;
                                                                                       }

                                                                                       var4 = 3;
                                                                                    }

                                                                                    if (!var1.hasRemaining()) {
                                                                                       break label871;
                                                                                    }

                                                                                    var10000 = var1.get();
                                                                                    if (var10000 != 110) {
                                                                                       if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                                                          var2.nextHeader = HTTP_STRING_75;
                                                                                          var2.state = 5;
                                                                                          break label849;
                                                                                       }

                                                                                       var4 = -1;
                                                                                       var7.append("Co").append((char)var10000);
                                                                                       break label782;
                                                                                    }

                                                                                    var4 = 4;
                                                                                 }

                                                                                 if (!var1.hasRemaining()) {
                                                                                    break label871;
                                                                                 }

                                                                                 var10000 = var1.get();
                                                                                 if (var10000 == 110) {
                                                                                    var4 = -2;
                                                                                    var6 = HTTP_STRING_78;
                                                                                    var8 = STATE_BYTES_77;
                                                                                    var5 = 4;
                                                                                    break label805;
                                                                                 }

                                                                                 if (var10000 != 116) {
                                                                                    if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                                                       var2.nextHeader = HTTP_STRING_76;
                                                                                       var2.state = 5;
                                                                                       break label849;
                                                                                    }

                                                                                    var4 = -1;
                                                                                    var7.append("Con").append((char)var10000);
                                                                                    break label782;
                                                                                 }

                                                                                 var4 = 5;
                                                                              }

                                                                              if (!var1.hasRemaining()) {
                                                                                 break label871;
                                                                              }

                                                                              var10000 = var1.get();
                                                                              if (var10000 != 101) {
                                                                                 if (var10000 != 58 && var10000 != 13 && var10000 != 10 && var10000 != 32) {
                                                                                    var4 = -1;
                                                                                    var7.append("Cont").append((char)var10000);
                                                                                    break label782;
                                                                                 }

                                                                                 var2.nextHeader = HTTP_STRING_91;
                                                                                 var2.state = 5;
                                                                                 break label849;
                                                                              }

                                                                              var4 = 6;
                                                                           }

                                                                           if (!var1.hasRemaining()) {
                                                                              break label871;
                                                                           }

                                                                           var10000 = var1.get();
                                                                           if (var10000 != 110) {
                                                                              if (var10000 != 58 && var10000 != 13 && var10000 != 10 && var10000 != 32) {
                                                                                 var4 = -1;
                                                                                 var7.append("Conte").append((char)var10000);
                                                                                 break label782;
                                                                              }

                                                                              var2.nextHeader = HTTP_STRING_92;
                                                                              var2.state = 5;
                                                                              break label849;
                                                                           }

                                                                           var4 = 7;
                                                                        }

                                                                        if (!var1.hasRemaining()) {
                                                                           break label871;
                                                                        }

                                                                        var10000 = var1.get();
                                                                        if (var10000 != 116) {
                                                                           if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                                              var2.nextHeader = HTTP_STRING_93;
                                                                              var2.state = 5;
                                                                              break label849;
                                                                           }

                                                                           var4 = -1;
                                                                           var7.append("Conten").append((char)var10000);
                                                                           break label782;
                                                                        }

                                                                        var4 = 8;
                                                                        break label821;
                                                                     }

                                                                     if (!var1.hasRemaining()) {
                                                                        break label871;
                                                                     }

                                                                     var10000 = var1.get();
                                                                     if (var10000 != 114) {
                                                                        if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                                           var2.nextHeader = HTTP_STRING_411;
                                                                           var2.state = 5;
                                                                           break label849;
                                                                        }

                                                                        var4 = -1;
                                                                        var7.append("T").append((char)var10000);
                                                                        break label782;
                                                                     }

                                                                     var4 = 20;
                                                                     break label872;
                                                                  }

                                                                  if (!var1.hasRemaining()) {
                                                                     break label871;
                                                                  }

                                                                  var10000 = var1.get();
                                                                  if (var10000 != 45) {
                                                                     if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                                        var2.nextHeader = HTTP_STRING_94;
                                                                        var2.state = 5;
                                                                        break label849;
                                                                     }

                                                                     var4 = -1;
                                                                     var7.append("Content").append((char)var10000);
                                                                     break label782;
                                                                  }

                                                                  var4 = 9;
                                                                  break label817;
                                                               }

                                                               if (!var1.hasRemaining()) {
                                                                  break label871;
                                                               }

                                                               var10000 = var1.get();
                                                               if (var10000 != 101) {
                                                                  if (var10000 != 116) {
                                                                     if (var10000 != 58 && var10000 != 13 && var10000 != 10 && var10000 != 32) {
                                                                        var4 = -1;
                                                                        var7.append("S").append((char)var10000);
                                                                        break label782;
                                                                     }

                                                                     var2.nextHeader = HTTP_STRING_335;
                                                                     var2.state = 5;
                                                                     break label849;
                                                                  }

                                                                  var4 = -2;
                                                                  var6 = HTTP_STRING_364;
                                                                  var8 = STATE_BYTES_363;
                                                                  var5 = 2;
                                                                  break label805;
                                                               }

                                                               var4 = 18;
                                                               break label809;
                                                            }

                                                            if (!var1.hasRemaining()) {
                                                               break label871;
                                                            }

                                                            var10000 = var1.get();
                                                            if (var10000 != 101) {
                                                               if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                                  var2.nextHeader = HTTP_STRING_305;
                                                                  var2.state = 5;
                                                                  break label849;
                                                               }

                                                               var4 = -1;
                                                               var7.append("R").append((char)var10000);
                                                               break label782;
                                                            }

                                                            var4 = 16;
                                                            break label813;
                                                         }

                                                         if (!var1.hasRemaining()) {
                                                            break label871;
                                                         }

                                                         var10000 = var1.get();
                                                         if (var10000 != 97) {
                                                            if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                               var2.nextHeader = HTTP_STRING_412;
                                                               var2.state = 5;
                                                               break label849;
                                                            }

                                                            var4 = -1;
                                                            var7.append("Tr").append((char)var10000);
                                                            break label782;
                                                         }

                                                         var4 = 21;
                                                         break label812;
                                                      }

                                                      if (!var1.hasRemaining()) {
                                                         break label871;
                                                      }

                                                      var10000 = var1.get();
                                                      if (var10000 == 68) {
                                                         var4 = -2;
                                                         var6 = HTTP_STRING_97;
                                                         var8 = STATE_BYTES_96;
                                                         var5 = 9;
                                                         break label805;
                                                      }

                                                      if (var10000 == 69) {
                                                         var4 = -2;
                                                         var6 = HTTP_STRING_119;
                                                         var8 = STATE_BYTES_118;
                                                         var5 = 9;
                                                         break label805;
                                                      }

                                                      if (var10000 != 76) {
                                                         if (var10000 != 77) {
                                                            if (var10000 != 82) {
                                                               if (var10000 != 84) {
                                                                  if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                                     var2.nextHeader = HTTP_STRING_95;
                                                                     var2.state = 5;
                                                                     break label849;
                                                                  }

                                                                  var4 = -1;
                                                                  var7.append("Content-").append((char)var10000);
                                                                  break label782;
                                                               }

                                                               var4 = -2;
                                                               var6 = HTTP_STRING_190;
                                                               var8 = STATE_BYTES_189;
                                                               var5 = 9;
                                                            } else {
                                                               var4 = -2;
                                                               var6 = HTTP_STRING_180;
                                                               var8 = STATE_BYTES_179;
                                                               var5 = 9;
                                                            }
                                                         } else {
                                                            var4 = -2;
                                                            var6 = HTTP_STRING_174;
                                                            var8 = STATE_BYTES_173;
                                                            var5 = 9;
                                                         }
                                                         break label805;
                                                      }

                                                      var4 = 10;
                                                      break label807;
                                                   }

                                                   if (!var1.hasRemaining()) {
                                                      break label871;
                                                   }

                                                   var10000 = var1.get();
                                                   if (var10000 != 114) {
                                                      if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                         var2.nextHeader = HTTP_STRING_263;
                                                         var2.state = 5;
                                                         break label849;
                                                      }

                                                      var4 = -1;
                                                      var7.append("P").append((char)var10000);
                                                      break label782;
                                                   }

                                                   var4 = 14;
                                                   break label806;
                                                }

                                                if (!var1.hasRemaining()) {
                                                   break label871;
                                                }

                                                var10000 = var1.get();
                                                if (var10000 != 99) {
                                                   if (var10000 != 103) {
                                                      if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                         var2.nextHeader = HTTP_STRING_21;
                                                         var2.state = 5;
                                                         break label849;
                                                      }

                                                      var4 = -1;
                                                      var7.append("A").append((char)var10000);
                                                      break label782;
                                                   }

                                                   var4 = -2;
                                                   var6 = HTTP_STRING_47;
                                                   var8 = STATE_BYTES_46;
                                                   var5 = 2;
                                                } else {
                                                   var4 = -2;
                                                   var6 = HTTP_STRING_23;
                                                   var8 = STATE_BYTES_22;
                                                   var5 = 2;
                                                }
                                                break label805;
                                             }

                                             if (!var1.hasRemaining()) {
                                                break label871;
                                             }

                                             var10000 = var1.get();
                                             if (var10000 != 120) {
                                                if (var10000 != 84) {
                                                   if (var10000 != 58 && var10000 != 13 && var10000 != 10 && var10000 != 32) {
                                                      var4 = -1;
                                                      var7.append("E").append((char)var10000);
                                                      break label782;
                                                   }

                                                   var2.nextHeader = HTTP_STRING_205;
                                                   var2.state = 5;
                                                   break label849;
                                                }

                                                var4 = -2;
                                                var6 = HTTP_STRING_219;
                                                var8 = STATE_BYTES_218;
                                                var5 = 2;
                                             } else {
                                                var4 = -2;
                                                var6 = HTTP_STRING_207;
                                                var8 = STATE_BYTES_206;
                                                var5 = 2;
                                             }
                                             break label805;
                                          }

                                          if (!var1.hasRemaining()) {
                                             break label871;
                                          }

                                          var10000 = var1.get();
                                          if (var10000 != 102) {
                                             if (var10000 != 116) {
                                                if (var10000 != 58 && var10000 != 13 && var10000 != 10 && var10000 != 32) {
                                                   var4 = -1;
                                                   var7.append("Re").append((char)var10000);
                                                   break label782;
                                                }

                                                var2.nextHeader = HTTP_STRING_306;
                                                var2.state = 5;
                                                break label849;
                                             }

                                             var4 = -2;
                                             var6 = HTTP_STRING_318;
                                             var8 = STATE_BYTES_317;
                                             var5 = 3;
                                          } else {
                                             var4 = -2;
                                             var6 = HTTP_STRING_308;
                                             var8 = STATE_BYTES_307;
                                             var5 = 3;
                                          }
                                          break label805;
                                       }

                                       if (!var1.hasRemaining()) {
                                          break label871;
                                       }

                                       var10000 = var1.get();
                                       if (var10000 != 105) {
                                          if (var10000 != 110) {
                                             if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                                var2.nextHeader = HTTP_STRING_413;
                                                var2.state = 5;
                                                break label849;
                                             }

                                             var4 = -1;
                                             var7.append("Tra").append((char)var10000);
                                             break label782;
                                          }

                                          var4 = -2;
                                          var6 = HTTP_STRING_423;
                                          var8 = STATE_BYTES_422;
                                          var5 = 4;
                                       } else {
                                          var4 = -2;
                                          var6 = HTTP_STRING_415;
                                          var8 = STATE_BYTES_414;
                                          var5 = 4;
                                       }
                                       break label805;
                                    }

                                    if (!var1.hasRemaining()) {
                                       break label871;
                                    }

                                    var10000 = var1.get();
                                    if (var10000 != 97) {
                                       if (var10000 != 111) {
                                          if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                             var2.nextHeader = HTTP_STRING_224;
                                             var2.state = 5;
                                             break label849;
                                          }

                                          var4 = -1;
                                          var7.append("L").append((char)var10000);
                                          break label782;
                                       }

                                       var4 = -2;
                                       var6 = HTTP_STRING_250;
                                       var8 = STATE_BYTES_249;
                                       var5 = 2;
                                    } else {
                                       var4 = -2;
                                       var6 = HTTP_STRING_226;
                                       var8 = STATE_BYTES_225;
                                       var5 = 2;
                                    }
                                    break label805;
                                 }

                                 if (!var1.hasRemaining()) {
                                    break label871;
                                 }

                                 var10000 = var1.get();
                                 if (var10000 != 97) {
                                    if (var10000 != 105) {
                                       if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                          var2.nextHeader = HTTP_STRING_450;
                                          var2.state = 5;
                                          break label849;
                                       }

                                       var4 = -1;
                                       var7.append("V").append((char)var10000);
                                       break label782;
                                    }

                                    var4 = -2;
                                    var6 = HTTP_STRING_458;
                                    var8 = STATE_BYTES_457;
                                    var5 = 2;
                                 } else {
                                    var4 = -2;
                                    var6 = HTTP_STRING_452;
                                    var8 = STATE_BYTES_451;
                                    var5 = 2;
                                 }
                                 break label805;
                              }

                              if (!var1.hasRemaining()) {
                                 break label871;
                              }

                              var10000 = var1.get();
                              if (var10000 != 114) {
                                 if (var10000 != 116) {
                                    if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                                       var2.nextHeader = HTTP_STRING_336;
                                       var2.state = 5;
                                       break label849;
                                    }

                                    var4 = -1;
                                    var7.append("Se").append((char)var10000);
                                    break label782;
                                 }

                                 var4 = -2;
                                 var6 = HTTP_STRING_346;
                                 var8 = STATE_BYTES_345;
                                 var5 = 3;
                              } else {
                                 var4 = -2;
                                 var6 = HTTP_STRING_338;
                                 var8 = STATE_BYTES_337;
                                 var5 = 3;
                              }
                              break label805;
                           }

                           if (!var1.hasRemaining()) {
                              break label871;
                           }

                           var10000 = var1.get();
                           if (var10000 != 97) {
                              if (var10000 != 87) {
                                 if (var10000 != 58 && var10000 != 13 && var10000 != 10 && var10000 != 32) {
                                    var4 = -1;
                                    var7.append("W").append((char)var10000);
                                    break label782;
                                 }

                                 var2.nextHeader = HTTP_STRING_461;
                                 var2.state = 5;
                                 break label849;
                              }

                              var4 = -2;
                              var6 = HTTP_STRING_475;
                              var8 = STATE_BYTES_474;
                              var5 = 2;
                           } else {
                              var4 = -2;
                              var6 = HTTP_STRING_463;
                              var8 = STATE_BYTES_462;
                              var5 = 2;
                           }
                           break label805;
                        }

                        if (!var1.hasRemaining()) {
                           break label871;
                        }

                        var10000 = var1.get();
                        if (var10000 != 97) {
                           if (var10000 != 101) {
                              if (var10000 != 111) {
                                 if (var10000 != 58 && var10000 != 13 && var10000 != 10 && var10000 != 32) {
                                    var4 = -1;
                                    var7.append("Content-L").append((char)var10000);
                                    break label782;
                                 }

                                 var2.nextHeader = HTTP_STRING_134;
                                 var2.state = 5;
                                 break label849;
                              }

                              var4 = -2;
                              var6 = HTTP_STRING_160;
                              var8 = STATE_BYTES_159;
                              var5 = 10;
                           } else {
                              var4 = -2;
                              var6 = HTTP_STRING_150;
                              var8 = STATE_BYTES_149;
                              var5 = 10;
                           }
                        } else {
                           var4 = -2;
                           var6 = HTTP_STRING_136;
                           var8 = STATE_BYTES_135;
                           var5 = 10;
                        }
                        break label805;
                     }

                     if (!var1.hasRemaining()) {
                        break label871;
                     }

                     var10000 = var1.get();
                     if (var10000 != 97) {
                        if (var10000 != 111) {
                           if (var10000 == 58 || var10000 == 13 || var10000 == 10 || var10000 == 32) {
                              var2.nextHeader = HTTP_STRING_264;
                              var2.state = 5;
                              break label849;
                           }

                           var4 = -1;
                           var7.append("Pr").append((char)var10000);
                           break label782;
                        }

                        var4 = -2;
                        var6 = HTTP_STRING_274;
                        var8 = STATE_BYTES_273;
                        var5 = 3;
                     } else {
                        var4 = -2;
                        var6 = HTTP_STRING_266;
                        var8 = STATE_BYTES_265;
                        var5 = 3;
                     }
                  }

                  while(true) {
                     if (!var1.hasRemaining()) {
                        break label871;
                     }

                     byte var10001 = var1.get();
                     if (var10001 != 58) {
                        if (var10001 == 32) {
                           throw new BadRequestException();
                        }

                        if (var10001 != 13 && var10001 != 10) {
                           if (var8.length != var5 && var10001 - var8[var5] == 0) {
                              ++var5;
                              if (!var1.hasRemaining()) {
                                 break label871;
                              }
                              continue;
                           }

                           var4 = -1;
                           var7.append(var6.toString().substring(0, var5)).append((char)var10001);
                           break;
                        }
                     }

                     boolean var9 = false;
                     if (var8.length != var5) {
                        var2.nextHeader = new HttpString(var8, 0, var5);
                        var2.state = 5;
                     } else {
                        var2.nextHeader = var6;
                        var2.state = 5;
                     }
                     break label849;
                  }
               }

               do {
                  if (!var1.hasRemaining()) {
                     break label871;
                  }

                  var10000 = var1.get();
                  if (var10000 == 58 || var10000 == 32 || var10000 == 13 || var10000 == 10) {
                     var2.nextHeader = new HttpString(var7.toString());
                     var2.state = 5;
                     break label849;
                  }

                  var7.append((char)var10000);
               } while(var1.hasRemaining());

               var2.parseState = var4;
               var10 = false;
               return;
            }

            var2.pos = 0;
            var2.current = null;
            var2.currentBytes = null;
            var7.setLength(0);
            var2.parseState = 0;
            return;
         }

         var2.pos = var5;
         var2.current = var6;
         var2.currentBytes = var8;
         var2.parseState = var4;
      }
   }

   static {
      Map var1 = HttpResponseParser.httpStrings();
      Object var10000 = var1.get("H");
      if (var10000 != null) {
         HTTP_STRING_3 = (HttpString)var10000;
      } else {
         HTTP_STRING_3 = new HttpString("H");
      }

      var10000 = var1.get("HT");
      if (var10000 != null) {
         HTTP_STRING_4 = (HttpString)var10000;
      } else {
         HTTP_STRING_4 = new HttpString("HT");
      }

      var10000 = var1.get("HTT");
      if (var10000 != null) {
         HTTP_STRING_5 = (HttpString)var10000;
      } else {
         HTTP_STRING_5 = new HttpString("HTT");
      }

      var10000 = var1.get("HTTP");
      if (var10000 != null) {
         HTTP_STRING_6 = (HttpString)var10000;
      } else {
         HTTP_STRING_6 = new HttpString("HTTP");
      }

      var10000 = var1.get("HTTP/");
      if (var10000 != null) {
         HTTP_STRING_7 = (HttpString)var10000;
      } else {
         HTTP_STRING_7 = new HttpString("HTTP/");
      }

      STATE_BYTES_8 = "HTTP/0.9".getBytes("ISO-8859-1");
      var10000 = var1.get("HTTP/0.9");
      if (var10000 != null) {
         HTTP_STRING_9 = (HttpString)var10000;
      } else {
         HTTP_STRING_9 = new HttpString("HTTP/0.9");
      }

      STATE_BYTES_10 = "HTTP/0.9".getBytes("ISO-8859-1");
      var10000 = var1.get("HTTP/0.9");
      if (var10000 != null) {
         HTTP_STRING_11 = (HttpString)var10000;
      } else {
         HTTP_STRING_11 = new HttpString("HTTP/0.9");
      }

      STATE_BYTES_12 = "HTTP/0.9".getBytes("ISO-8859-1");
      var10000 = var1.get("HTTP/0.9");
      if (var10000 != null) {
         HTTP_STRING_13 = (HttpString)var10000;
      } else {
         HTTP_STRING_13 = new HttpString("HTTP/0.9");
      }

      var10000 = var1.get("HTTP/1");
      if (var10000 != null) {
         HTTP_STRING_14 = (HttpString)var10000;
      } else {
         HTTP_STRING_14 = new HttpString("HTTP/1");
      }

      var10000 = var1.get("HTTP/1.");
      if (var10000 != null) {
         HTTP_STRING_15 = (HttpString)var10000;
      } else {
         HTTP_STRING_15 = new HttpString("HTTP/1.");
      }

      STATE_BYTES_16 = "HTTP/1.0".getBytes("ISO-8859-1");
      var10000 = var1.get("HTTP/1.0");
      if (var10000 != null) {
         HTTP_STRING_17 = (HttpString)var10000;
      } else {
         HTTP_STRING_17 = new HttpString("HTTP/1.0");
      }

      STATE_BYTES_18 = "HTTP/1.1".getBytes("ISO-8859-1");
      var10000 = var1.get("HTTP/1.1");
      if (var10000 != null) {
         HTTP_STRING_19 = (HttpString)var10000;
      } else {
         HTTP_STRING_19 = new HttpString("HTTP/1.1");
      }

      var10000 = var1.get("A");
      if (var10000 != null) {
         HTTP_STRING_21 = (HttpString)var10000;
      } else {
         HTTP_STRING_21 = new HttpString("A");
      }

      STATE_BYTES_22 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_23 = (HttpString)var10000;
      } else {
         HTTP_STRING_23 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_24 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_25 = (HttpString)var10000;
      } else {
         HTTP_STRING_25 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_26 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_27 = (HttpString)var10000;
      } else {
         HTTP_STRING_27 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_28 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_29 = (HttpString)var10000;
      } else {
         HTTP_STRING_29 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_30 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_31 = (HttpString)var10000;
      } else {
         HTTP_STRING_31 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_32 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_33 = (HttpString)var10000;
      } else {
         HTTP_STRING_33 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_34 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_35 = (HttpString)var10000;
      } else {
         HTTP_STRING_35 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_36 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_37 = (HttpString)var10000;
      } else {
         HTTP_STRING_37 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_38 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_39 = (HttpString)var10000;
      } else {
         HTTP_STRING_39 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_40 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_41 = (HttpString)var10000;
      } else {
         HTTP_STRING_41 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_42 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_43 = (HttpString)var10000;
      } else {
         HTTP_STRING_43 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_44 = "Accept-Ranges".getBytes("ISO-8859-1");
      var10000 = var1.get("Accept-Ranges");
      if (var10000 != null) {
         HTTP_STRING_45 = (HttpString)var10000;
      } else {
         HTTP_STRING_45 = new HttpString("Accept-Ranges");
      }

      STATE_BYTES_46 = "Age".getBytes("ISO-8859-1");
      var10000 = var1.get("Age");
      if (var10000 != null) {
         HTTP_STRING_47 = (HttpString)var10000;
      } else {
         HTTP_STRING_47 = new HttpString("Age");
      }

      STATE_BYTES_48 = "Age".getBytes("ISO-8859-1");
      var10000 = var1.get("Age");
      if (var10000 != null) {
         HTTP_STRING_49 = (HttpString)var10000;
      } else {
         HTTP_STRING_49 = new HttpString("Age");
      }

      var10000 = var1.get("C");
      if (var10000 != null) {
         HTTP_STRING_50 = (HttpString)var10000;
      } else {
         HTTP_STRING_50 = new HttpString("C");
      }

      STATE_BYTES_51 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_52 = (HttpString)var10000;
      } else {
         HTTP_STRING_52 = new HttpString("Cache-Control");
      }

      STATE_BYTES_53 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_54 = (HttpString)var10000;
      } else {
         HTTP_STRING_54 = new HttpString("Cache-Control");
      }

      STATE_BYTES_55 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_56 = (HttpString)var10000;
      } else {
         HTTP_STRING_56 = new HttpString("Cache-Control");
      }

      STATE_BYTES_57 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_58 = (HttpString)var10000;
      } else {
         HTTP_STRING_58 = new HttpString("Cache-Control");
      }

      STATE_BYTES_59 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_60 = (HttpString)var10000;
      } else {
         HTTP_STRING_60 = new HttpString("Cache-Control");
      }

      STATE_BYTES_61 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_62 = (HttpString)var10000;
      } else {
         HTTP_STRING_62 = new HttpString("Cache-Control");
      }

      STATE_BYTES_63 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_64 = (HttpString)var10000;
      } else {
         HTTP_STRING_64 = new HttpString("Cache-Control");
      }

      STATE_BYTES_65 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_66 = (HttpString)var10000;
      } else {
         HTTP_STRING_66 = new HttpString("Cache-Control");
      }

      STATE_BYTES_67 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_68 = (HttpString)var10000;
      } else {
         HTTP_STRING_68 = new HttpString("Cache-Control");
      }

      STATE_BYTES_69 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_70 = (HttpString)var10000;
      } else {
         HTTP_STRING_70 = new HttpString("Cache-Control");
      }

      STATE_BYTES_71 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_72 = (HttpString)var10000;
      } else {
         HTTP_STRING_72 = new HttpString("Cache-Control");
      }

      STATE_BYTES_73 = "Cache-Control".getBytes("ISO-8859-1");
      var10000 = var1.get("Cache-Control");
      if (var10000 != null) {
         HTTP_STRING_74 = (HttpString)var10000;
      } else {
         HTTP_STRING_74 = new HttpString("Cache-Control");
      }

      var10000 = var1.get("Co");
      if (var10000 != null) {
         HTTP_STRING_75 = (HttpString)var10000;
      } else {
         HTTP_STRING_75 = new HttpString("Co");
      }

      var10000 = var1.get("Con");
      if (var10000 != null) {
         HTTP_STRING_76 = (HttpString)var10000;
      } else {
         HTTP_STRING_76 = new HttpString("Con");
      }

      STATE_BYTES_77 = "Connection".getBytes("ISO-8859-1");
      var10000 = var1.get("Connection");
      if (var10000 != null) {
         HTTP_STRING_78 = (HttpString)var10000;
      } else {
         HTTP_STRING_78 = new HttpString("Connection");
      }

      STATE_BYTES_79 = "Connection".getBytes("ISO-8859-1");
      var10000 = var1.get("Connection");
      if (var10000 != null) {
         HTTP_STRING_80 = (HttpString)var10000;
      } else {
         HTTP_STRING_80 = new HttpString("Connection");
      }

      STATE_BYTES_81 = "Connection".getBytes("ISO-8859-1");
      var10000 = var1.get("Connection");
      if (var10000 != null) {
         HTTP_STRING_82 = (HttpString)var10000;
      } else {
         HTTP_STRING_82 = new HttpString("Connection");
      }

      STATE_BYTES_83 = "Connection".getBytes("ISO-8859-1");
      var10000 = var1.get("Connection");
      if (var10000 != null) {
         HTTP_STRING_84 = (HttpString)var10000;
      } else {
         HTTP_STRING_84 = new HttpString("Connection");
      }

      STATE_BYTES_85 = "Connection".getBytes("ISO-8859-1");
      var10000 = var1.get("Connection");
      if (var10000 != null) {
         HTTP_STRING_86 = (HttpString)var10000;
      } else {
         HTTP_STRING_86 = new HttpString("Connection");
      }

      STATE_BYTES_87 = "Connection".getBytes("ISO-8859-1");
      var10000 = var1.get("Connection");
      if (var10000 != null) {
         HTTP_STRING_88 = (HttpString)var10000;
      } else {
         HTTP_STRING_88 = new HttpString("Connection");
      }

      STATE_BYTES_89 = "Connection".getBytes("ISO-8859-1");
      var10000 = var1.get("Connection");
      if (var10000 != null) {
         HTTP_STRING_90 = (HttpString)var10000;
      } else {
         HTTP_STRING_90 = new HttpString("Connection");
      }

      var10000 = var1.get("Cont");
      if (var10000 != null) {
         HTTP_STRING_91 = (HttpString)var10000;
      } else {
         HTTP_STRING_91 = new HttpString("Cont");
      }

      var10000 = var1.get("Conte");
      if (var10000 != null) {
         HTTP_STRING_92 = (HttpString)var10000;
      } else {
         HTTP_STRING_92 = new HttpString("Conte");
      }

      var10000 = var1.get("Conten");
      if (var10000 != null) {
         HTTP_STRING_93 = (HttpString)var10000;
      } else {
         HTTP_STRING_93 = new HttpString("Conten");
      }

      var10000 = var1.get("Content");
      if (var10000 != null) {
         HTTP_STRING_94 = (HttpString)var10000;
      } else {
         HTTP_STRING_94 = new HttpString("Content");
      }

      var10000 = var1.get("Content-");
      if (var10000 != null) {
         HTTP_STRING_95 = (HttpString)var10000;
      } else {
         HTTP_STRING_95 = new HttpString("Content-");
      }

      STATE_BYTES_96 = "Content-Disposition".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Disposition");
      if (var10000 != null) {
         HTTP_STRING_97 = (HttpString)var10000;
      } else {
         HTTP_STRING_97 = new HttpString("Content-Disposition");
      }

      STATE_BYTES_98 = "Content-Disposition".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Disposition");
      if (var10000 != null) {
         HTTP_STRING_99 = (HttpString)var10000;
      } else {
         HTTP_STRING_99 = new HttpString("Content-Disposition");
      }

      STATE_BYTES_100 = "Content-Disposition".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Disposition");
      if (var10000 != null) {
         HTTP_STRING_101 = (HttpString)var10000;
      } else {
         HTTP_STRING_101 = new HttpString("Content-Disposition");
      }

      STATE_BYTES_102 = "Content-Disposition".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Disposition");
      if (var10000 != null) {
         HTTP_STRING_103 = (HttpString)var10000;
      } else {
         HTTP_STRING_103 = new HttpString("Content-Disposition");
      }

      STATE_BYTES_104 = "Content-Disposition".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Disposition");
      if (var10000 != null) {
         HTTP_STRING_105 = (HttpString)var10000;
      } else {
         HTTP_STRING_105 = new HttpString("Content-Disposition");
      }

      STATE_BYTES_106 = "Content-Disposition".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Disposition");
      if (var10000 != null) {
         HTTP_STRING_107 = (HttpString)var10000;
      } else {
         HTTP_STRING_107 = new HttpString("Content-Disposition");
      }

      STATE_BYTES_108 = "Content-Disposition".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Disposition");
      if (var10000 != null) {
         HTTP_STRING_109 = (HttpString)var10000;
      } else {
         HTTP_STRING_109 = new HttpString("Content-Disposition");
      }

      STATE_BYTES_110 = "Content-Disposition".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Disposition");
      if (var10000 != null) {
         HTTP_STRING_111 = (HttpString)var10000;
      } else {
         HTTP_STRING_111 = new HttpString("Content-Disposition");
      }

      STATE_BYTES_112 = "Content-Disposition".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Disposition");
      if (var10000 != null) {
         HTTP_STRING_113 = (HttpString)var10000;
      } else {
         HTTP_STRING_113 = new HttpString("Content-Disposition");
      }

      STATE_BYTES_114 = "Content-Disposition".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Disposition");
      if (var10000 != null) {
         HTTP_STRING_115 = (HttpString)var10000;
      } else {
         HTTP_STRING_115 = new HttpString("Content-Disposition");
      }

      STATE_BYTES_116 = "Content-Disposition".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Disposition");
      if (var10000 != null) {
         HTTP_STRING_117 = (HttpString)var10000;
      } else {
         HTTP_STRING_117 = new HttpString("Content-Disposition");
      }

      STATE_BYTES_118 = "Content-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Encoding");
      if (var10000 != null) {
         HTTP_STRING_119 = (HttpString)var10000;
      } else {
         HTTP_STRING_119 = new HttpString("Content-Encoding");
      }

      STATE_BYTES_120 = "Content-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Encoding");
      if (var10000 != null) {
         HTTP_STRING_121 = (HttpString)var10000;
      } else {
         HTTP_STRING_121 = new HttpString("Content-Encoding");
      }

      STATE_BYTES_122 = "Content-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Encoding");
      if (var10000 != null) {
         HTTP_STRING_123 = (HttpString)var10000;
      } else {
         HTTP_STRING_123 = new HttpString("Content-Encoding");
      }

      STATE_BYTES_124 = "Content-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Encoding");
      if (var10000 != null) {
         HTTP_STRING_125 = (HttpString)var10000;
      } else {
         HTTP_STRING_125 = new HttpString("Content-Encoding");
      }

      STATE_BYTES_126 = "Content-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Encoding");
      if (var10000 != null) {
         HTTP_STRING_127 = (HttpString)var10000;
      } else {
         HTTP_STRING_127 = new HttpString("Content-Encoding");
      }

      STATE_BYTES_128 = "Content-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Encoding");
      if (var10000 != null) {
         HTTP_STRING_129 = (HttpString)var10000;
      } else {
         HTTP_STRING_129 = new HttpString("Content-Encoding");
      }

      STATE_BYTES_130 = "Content-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Encoding");
      if (var10000 != null) {
         HTTP_STRING_131 = (HttpString)var10000;
      } else {
         HTTP_STRING_131 = new HttpString("Content-Encoding");
      }

      STATE_BYTES_132 = "Content-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Encoding");
      if (var10000 != null) {
         HTTP_STRING_133 = (HttpString)var10000;
      } else {
         HTTP_STRING_133 = new HttpString("Content-Encoding");
      }

      var10000 = var1.get("Content-L");
      if (var10000 != null) {
         HTTP_STRING_134 = (HttpString)var10000;
      } else {
         HTTP_STRING_134 = new HttpString("Content-L");
      }

      STATE_BYTES_135 = "Content-Language".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Language");
      if (var10000 != null) {
         HTTP_STRING_136 = (HttpString)var10000;
      } else {
         HTTP_STRING_136 = new HttpString("Content-Language");
      }

      STATE_BYTES_137 = "Content-Language".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Language");
      if (var10000 != null) {
         HTTP_STRING_138 = (HttpString)var10000;
      } else {
         HTTP_STRING_138 = new HttpString("Content-Language");
      }

      STATE_BYTES_139 = "Content-Language".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Language");
      if (var10000 != null) {
         HTTP_STRING_140 = (HttpString)var10000;
      } else {
         HTTP_STRING_140 = new HttpString("Content-Language");
      }

      STATE_BYTES_141 = "Content-Language".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Language");
      if (var10000 != null) {
         HTTP_STRING_142 = (HttpString)var10000;
      } else {
         HTTP_STRING_142 = new HttpString("Content-Language");
      }

      STATE_BYTES_143 = "Content-Language".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Language");
      if (var10000 != null) {
         HTTP_STRING_144 = (HttpString)var10000;
      } else {
         HTTP_STRING_144 = new HttpString("Content-Language");
      }

      STATE_BYTES_145 = "Content-Language".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Language");
      if (var10000 != null) {
         HTTP_STRING_146 = (HttpString)var10000;
      } else {
         HTTP_STRING_146 = new HttpString("Content-Language");
      }

      STATE_BYTES_147 = "Content-Language".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Language");
      if (var10000 != null) {
         HTTP_STRING_148 = (HttpString)var10000;
      } else {
         HTTP_STRING_148 = new HttpString("Content-Language");
      }

      STATE_BYTES_149 = "Content-Length".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Length");
      if (var10000 != null) {
         HTTP_STRING_150 = (HttpString)var10000;
      } else {
         HTTP_STRING_150 = new HttpString("Content-Length");
      }

      STATE_BYTES_151 = "Content-Length".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Length");
      if (var10000 != null) {
         HTTP_STRING_152 = (HttpString)var10000;
      } else {
         HTTP_STRING_152 = new HttpString("Content-Length");
      }

      STATE_BYTES_153 = "Content-Length".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Length");
      if (var10000 != null) {
         HTTP_STRING_154 = (HttpString)var10000;
      } else {
         HTTP_STRING_154 = new HttpString("Content-Length");
      }

      STATE_BYTES_155 = "Content-Length".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Length");
      if (var10000 != null) {
         HTTP_STRING_156 = (HttpString)var10000;
      } else {
         HTTP_STRING_156 = new HttpString("Content-Length");
      }

      STATE_BYTES_157 = "Content-Length".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Length");
      if (var10000 != null) {
         HTTP_STRING_158 = (HttpString)var10000;
      } else {
         HTTP_STRING_158 = new HttpString("Content-Length");
      }

      STATE_BYTES_159 = "Content-Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Location");
      if (var10000 != null) {
         HTTP_STRING_160 = (HttpString)var10000;
      } else {
         HTTP_STRING_160 = new HttpString("Content-Location");
      }

      STATE_BYTES_161 = "Content-Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Location");
      if (var10000 != null) {
         HTTP_STRING_162 = (HttpString)var10000;
      } else {
         HTTP_STRING_162 = new HttpString("Content-Location");
      }

      STATE_BYTES_163 = "Content-Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Location");
      if (var10000 != null) {
         HTTP_STRING_164 = (HttpString)var10000;
      } else {
         HTTP_STRING_164 = new HttpString("Content-Location");
      }

      STATE_BYTES_165 = "Content-Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Location");
      if (var10000 != null) {
         HTTP_STRING_166 = (HttpString)var10000;
      } else {
         HTTP_STRING_166 = new HttpString("Content-Location");
      }

      STATE_BYTES_167 = "Content-Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Location");
      if (var10000 != null) {
         HTTP_STRING_168 = (HttpString)var10000;
      } else {
         HTTP_STRING_168 = new HttpString("Content-Location");
      }

      STATE_BYTES_169 = "Content-Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Location");
      if (var10000 != null) {
         HTTP_STRING_170 = (HttpString)var10000;
      } else {
         HTTP_STRING_170 = new HttpString("Content-Location");
      }

      STATE_BYTES_171 = "Content-Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Location");
      if (var10000 != null) {
         HTTP_STRING_172 = (HttpString)var10000;
      } else {
         HTTP_STRING_172 = new HttpString("Content-Location");
      }

      STATE_BYTES_173 = "Content-MD5".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-MD5");
      if (var10000 != null) {
         HTTP_STRING_174 = (HttpString)var10000;
      } else {
         HTTP_STRING_174 = new HttpString("Content-MD5");
      }

      STATE_BYTES_175 = "Content-MD5".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-MD5");
      if (var10000 != null) {
         HTTP_STRING_176 = (HttpString)var10000;
      } else {
         HTTP_STRING_176 = new HttpString("Content-MD5");
      }

      STATE_BYTES_177 = "Content-MD5".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-MD5");
      if (var10000 != null) {
         HTTP_STRING_178 = (HttpString)var10000;
      } else {
         HTTP_STRING_178 = new HttpString("Content-MD5");
      }

      STATE_BYTES_179 = "Content-Range".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Range");
      if (var10000 != null) {
         HTTP_STRING_180 = (HttpString)var10000;
      } else {
         HTTP_STRING_180 = new HttpString("Content-Range");
      }

      STATE_BYTES_181 = "Content-Range".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Range");
      if (var10000 != null) {
         HTTP_STRING_182 = (HttpString)var10000;
      } else {
         HTTP_STRING_182 = new HttpString("Content-Range");
      }

      STATE_BYTES_183 = "Content-Range".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Range");
      if (var10000 != null) {
         HTTP_STRING_184 = (HttpString)var10000;
      } else {
         HTTP_STRING_184 = new HttpString("Content-Range");
      }

      STATE_BYTES_185 = "Content-Range".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Range");
      if (var10000 != null) {
         HTTP_STRING_186 = (HttpString)var10000;
      } else {
         HTTP_STRING_186 = new HttpString("Content-Range");
      }

      STATE_BYTES_187 = "Content-Range".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Range");
      if (var10000 != null) {
         HTTP_STRING_188 = (HttpString)var10000;
      } else {
         HTTP_STRING_188 = new HttpString("Content-Range");
      }

      STATE_BYTES_189 = "Content-Type".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Type");
      if (var10000 != null) {
         HTTP_STRING_190 = (HttpString)var10000;
      } else {
         HTTP_STRING_190 = new HttpString("Content-Type");
      }

      STATE_BYTES_191 = "Content-Type".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Type");
      if (var10000 != null) {
         HTTP_STRING_192 = (HttpString)var10000;
      } else {
         HTTP_STRING_192 = new HttpString("Content-Type");
      }

      STATE_BYTES_193 = "Content-Type".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Type");
      if (var10000 != null) {
         HTTP_STRING_194 = (HttpString)var10000;
      } else {
         HTTP_STRING_194 = new HttpString("Content-Type");
      }

      STATE_BYTES_195 = "Content-Type".getBytes("ISO-8859-1");
      var10000 = var1.get("Content-Type");
      if (var10000 != null) {
         HTTP_STRING_196 = (HttpString)var10000;
      } else {
         HTTP_STRING_196 = new HttpString("Content-Type");
      }

      STATE_BYTES_197 = "Date".getBytes("ISO-8859-1");
      var10000 = var1.get("Date");
      if (var10000 != null) {
         HTTP_STRING_198 = (HttpString)var10000;
      } else {
         HTTP_STRING_198 = new HttpString("Date");
      }

      STATE_BYTES_199 = "Date".getBytes("ISO-8859-1");
      var10000 = var1.get("Date");
      if (var10000 != null) {
         HTTP_STRING_200 = (HttpString)var10000;
      } else {
         HTTP_STRING_200 = new HttpString("Date");
      }

      STATE_BYTES_201 = "Date".getBytes("ISO-8859-1");
      var10000 = var1.get("Date");
      if (var10000 != null) {
         HTTP_STRING_202 = (HttpString)var10000;
      } else {
         HTTP_STRING_202 = new HttpString("Date");
      }

      STATE_BYTES_203 = "Date".getBytes("ISO-8859-1");
      var10000 = var1.get("Date");
      if (var10000 != null) {
         HTTP_STRING_204 = (HttpString)var10000;
      } else {
         HTTP_STRING_204 = new HttpString("Date");
      }

      var10000 = var1.get("E");
      if (var10000 != null) {
         HTTP_STRING_205 = (HttpString)var10000;
      } else {
         HTTP_STRING_205 = new HttpString("E");
      }

      STATE_BYTES_206 = "Expires".getBytes("ISO-8859-1");
      var10000 = var1.get("Expires");
      if (var10000 != null) {
         HTTP_STRING_207 = (HttpString)var10000;
      } else {
         HTTP_STRING_207 = new HttpString("Expires");
      }

      STATE_BYTES_208 = "Expires".getBytes("ISO-8859-1");
      var10000 = var1.get("Expires");
      if (var10000 != null) {
         HTTP_STRING_209 = (HttpString)var10000;
      } else {
         HTTP_STRING_209 = new HttpString("Expires");
      }

      STATE_BYTES_210 = "Expires".getBytes("ISO-8859-1");
      var10000 = var1.get("Expires");
      if (var10000 != null) {
         HTTP_STRING_211 = (HttpString)var10000;
      } else {
         HTTP_STRING_211 = new HttpString("Expires");
      }

      STATE_BYTES_212 = "Expires".getBytes("ISO-8859-1");
      var10000 = var1.get("Expires");
      if (var10000 != null) {
         HTTP_STRING_213 = (HttpString)var10000;
      } else {
         HTTP_STRING_213 = new HttpString("Expires");
      }

      STATE_BYTES_214 = "Expires".getBytes("ISO-8859-1");
      var10000 = var1.get("Expires");
      if (var10000 != null) {
         HTTP_STRING_215 = (HttpString)var10000;
      } else {
         HTTP_STRING_215 = new HttpString("Expires");
      }

      STATE_BYTES_216 = "Expires".getBytes("ISO-8859-1");
      var10000 = var1.get("Expires");
      if (var10000 != null) {
         HTTP_STRING_217 = (HttpString)var10000;
      } else {
         HTTP_STRING_217 = new HttpString("Expires");
      }

      STATE_BYTES_218 = "ETag".getBytes("ISO-8859-1");
      var10000 = var1.get("ETag");
      if (var10000 != null) {
         HTTP_STRING_219 = (HttpString)var10000;
      } else {
         HTTP_STRING_219 = new HttpString("ETag");
      }

      STATE_BYTES_220 = "ETag".getBytes("ISO-8859-1");
      var10000 = var1.get("ETag");
      if (var10000 != null) {
         HTTP_STRING_221 = (HttpString)var10000;
      } else {
         HTTP_STRING_221 = new HttpString("ETag");
      }

      STATE_BYTES_222 = "ETag".getBytes("ISO-8859-1");
      var10000 = var1.get("ETag");
      if (var10000 != null) {
         HTTP_STRING_223 = (HttpString)var10000;
      } else {
         HTTP_STRING_223 = new HttpString("ETag");
      }

      var10000 = var1.get("L");
      if (var10000 != null) {
         HTTP_STRING_224 = (HttpString)var10000;
      } else {
         HTTP_STRING_224 = new HttpString("L");
      }

      STATE_BYTES_225 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_226 = (HttpString)var10000;
      } else {
         HTTP_STRING_226 = new HttpString("Last-Modified");
      }

      STATE_BYTES_227 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_228 = (HttpString)var10000;
      } else {
         HTTP_STRING_228 = new HttpString("Last-Modified");
      }

      STATE_BYTES_229 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_230 = (HttpString)var10000;
      } else {
         HTTP_STRING_230 = new HttpString("Last-Modified");
      }

      STATE_BYTES_231 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_232 = (HttpString)var10000;
      } else {
         HTTP_STRING_232 = new HttpString("Last-Modified");
      }

      STATE_BYTES_233 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_234 = (HttpString)var10000;
      } else {
         HTTP_STRING_234 = new HttpString("Last-Modified");
      }

      STATE_BYTES_235 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_236 = (HttpString)var10000;
      } else {
         HTTP_STRING_236 = new HttpString("Last-Modified");
      }

      STATE_BYTES_237 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_238 = (HttpString)var10000;
      } else {
         HTTP_STRING_238 = new HttpString("Last-Modified");
      }

      STATE_BYTES_239 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_240 = (HttpString)var10000;
      } else {
         HTTP_STRING_240 = new HttpString("Last-Modified");
      }

      STATE_BYTES_241 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_242 = (HttpString)var10000;
      } else {
         HTTP_STRING_242 = new HttpString("Last-Modified");
      }

      STATE_BYTES_243 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_244 = (HttpString)var10000;
      } else {
         HTTP_STRING_244 = new HttpString("Last-Modified");
      }

      STATE_BYTES_245 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_246 = (HttpString)var10000;
      } else {
         HTTP_STRING_246 = new HttpString("Last-Modified");
      }

      STATE_BYTES_247 = "Last-Modified".getBytes("ISO-8859-1");
      var10000 = var1.get("Last-Modified");
      if (var10000 != null) {
         HTTP_STRING_248 = (HttpString)var10000;
      } else {
         HTTP_STRING_248 = new HttpString("Last-Modified");
      }

      STATE_BYTES_249 = "Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Location");
      if (var10000 != null) {
         HTTP_STRING_250 = (HttpString)var10000;
      } else {
         HTTP_STRING_250 = new HttpString("Location");
      }

      STATE_BYTES_251 = "Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Location");
      if (var10000 != null) {
         HTTP_STRING_252 = (HttpString)var10000;
      } else {
         HTTP_STRING_252 = new HttpString("Location");
      }

      STATE_BYTES_253 = "Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Location");
      if (var10000 != null) {
         HTTP_STRING_254 = (HttpString)var10000;
      } else {
         HTTP_STRING_254 = new HttpString("Location");
      }

      STATE_BYTES_255 = "Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Location");
      if (var10000 != null) {
         HTTP_STRING_256 = (HttpString)var10000;
      } else {
         HTTP_STRING_256 = new HttpString("Location");
      }

      STATE_BYTES_257 = "Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Location");
      if (var10000 != null) {
         HTTP_STRING_258 = (HttpString)var10000;
      } else {
         HTTP_STRING_258 = new HttpString("Location");
      }

      STATE_BYTES_259 = "Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Location");
      if (var10000 != null) {
         HTTP_STRING_260 = (HttpString)var10000;
      } else {
         HTTP_STRING_260 = new HttpString("Location");
      }

      STATE_BYTES_261 = "Location".getBytes("ISO-8859-1");
      var10000 = var1.get("Location");
      if (var10000 != null) {
         HTTP_STRING_262 = (HttpString)var10000;
      } else {
         HTTP_STRING_262 = new HttpString("Location");
      }

      var10000 = var1.get("P");
      if (var10000 != null) {
         HTTP_STRING_263 = (HttpString)var10000;
      } else {
         HTTP_STRING_263 = new HttpString("P");
      }

      var10000 = var1.get("Pr");
      if (var10000 != null) {
         HTTP_STRING_264 = (HttpString)var10000;
      } else {
         HTTP_STRING_264 = new HttpString("Pr");
      }

      STATE_BYTES_265 = "Pragma".getBytes("ISO-8859-1");
      var10000 = var1.get("Pragma");
      if (var10000 != null) {
         HTTP_STRING_266 = (HttpString)var10000;
      } else {
         HTTP_STRING_266 = new HttpString("Pragma");
      }

      STATE_BYTES_267 = "Pragma".getBytes("ISO-8859-1");
      var10000 = var1.get("Pragma");
      if (var10000 != null) {
         HTTP_STRING_268 = (HttpString)var10000;
      } else {
         HTTP_STRING_268 = new HttpString("Pragma");
      }

      STATE_BYTES_269 = "Pragma".getBytes("ISO-8859-1");
      var10000 = var1.get("Pragma");
      if (var10000 != null) {
         HTTP_STRING_270 = (HttpString)var10000;
      } else {
         HTTP_STRING_270 = new HttpString("Pragma");
      }

      STATE_BYTES_271 = "Pragma".getBytes("ISO-8859-1");
      var10000 = var1.get("Pragma");
      if (var10000 != null) {
         HTTP_STRING_272 = (HttpString)var10000;
      } else {
         HTTP_STRING_272 = new HttpString("Pragma");
      }

      STATE_BYTES_273 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_274 = (HttpString)var10000;
      } else {
         HTTP_STRING_274 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_275 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_276 = (HttpString)var10000;
      } else {
         HTTP_STRING_276 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_277 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_278 = (HttpString)var10000;
      } else {
         HTTP_STRING_278 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_279 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_280 = (HttpString)var10000;
      } else {
         HTTP_STRING_280 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_281 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_282 = (HttpString)var10000;
      } else {
         HTTP_STRING_282 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_283 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_284 = (HttpString)var10000;
      } else {
         HTTP_STRING_284 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_285 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_286 = (HttpString)var10000;
      } else {
         HTTP_STRING_286 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_287 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_288 = (HttpString)var10000;
      } else {
         HTTP_STRING_288 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_289 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_290 = (HttpString)var10000;
      } else {
         HTTP_STRING_290 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_291 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_292 = (HttpString)var10000;
      } else {
         HTTP_STRING_292 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_293 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_294 = (HttpString)var10000;
      } else {
         HTTP_STRING_294 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_295 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_296 = (HttpString)var10000;
      } else {
         HTTP_STRING_296 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_297 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_298 = (HttpString)var10000;
      } else {
         HTTP_STRING_298 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_299 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_300 = (HttpString)var10000;
      } else {
         HTTP_STRING_300 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_301 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_302 = (HttpString)var10000;
      } else {
         HTTP_STRING_302 = new HttpString("Proxy-Authenticate");
      }

      STATE_BYTES_303 = "Proxy-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("Proxy-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_304 = (HttpString)var10000;
      } else {
         HTTP_STRING_304 = new HttpString("Proxy-Authenticate");
      }

      var10000 = var1.get("R");
      if (var10000 != null) {
         HTTP_STRING_305 = (HttpString)var10000;
      } else {
         HTTP_STRING_305 = new HttpString("R");
      }

      var10000 = var1.get("Re");
      if (var10000 != null) {
         HTTP_STRING_306 = (HttpString)var10000;
      } else {
         HTTP_STRING_306 = new HttpString("Re");
      }

      STATE_BYTES_307 = "Refresh".getBytes("ISO-8859-1");
      var10000 = var1.get("Refresh");
      if (var10000 != null) {
         HTTP_STRING_308 = (HttpString)var10000;
      } else {
         HTTP_STRING_308 = new HttpString("Refresh");
      }

      STATE_BYTES_309 = "Refresh".getBytes("ISO-8859-1");
      var10000 = var1.get("Refresh");
      if (var10000 != null) {
         HTTP_STRING_310 = (HttpString)var10000;
      } else {
         HTTP_STRING_310 = new HttpString("Refresh");
      }

      STATE_BYTES_311 = "Refresh".getBytes("ISO-8859-1");
      var10000 = var1.get("Refresh");
      if (var10000 != null) {
         HTTP_STRING_312 = (HttpString)var10000;
      } else {
         HTTP_STRING_312 = new HttpString("Refresh");
      }

      STATE_BYTES_313 = "Refresh".getBytes("ISO-8859-1");
      var10000 = var1.get("Refresh");
      if (var10000 != null) {
         HTTP_STRING_314 = (HttpString)var10000;
      } else {
         HTTP_STRING_314 = new HttpString("Refresh");
      }

      STATE_BYTES_315 = "Refresh".getBytes("ISO-8859-1");
      var10000 = var1.get("Refresh");
      if (var10000 != null) {
         HTTP_STRING_316 = (HttpString)var10000;
      } else {
         HTTP_STRING_316 = new HttpString("Refresh");
      }

      STATE_BYTES_317 = "Retry-After".getBytes("ISO-8859-1");
      var10000 = var1.get("Retry-After");
      if (var10000 != null) {
         HTTP_STRING_318 = (HttpString)var10000;
      } else {
         HTTP_STRING_318 = new HttpString("Retry-After");
      }

      STATE_BYTES_319 = "Retry-After".getBytes("ISO-8859-1");
      var10000 = var1.get("Retry-After");
      if (var10000 != null) {
         HTTP_STRING_320 = (HttpString)var10000;
      } else {
         HTTP_STRING_320 = new HttpString("Retry-After");
      }

      STATE_BYTES_321 = "Retry-After".getBytes("ISO-8859-1");
      var10000 = var1.get("Retry-After");
      if (var10000 != null) {
         HTTP_STRING_322 = (HttpString)var10000;
      } else {
         HTTP_STRING_322 = new HttpString("Retry-After");
      }

      STATE_BYTES_323 = "Retry-After".getBytes("ISO-8859-1");
      var10000 = var1.get("Retry-After");
      if (var10000 != null) {
         HTTP_STRING_324 = (HttpString)var10000;
      } else {
         HTTP_STRING_324 = new HttpString("Retry-After");
      }

      STATE_BYTES_325 = "Retry-After".getBytes("ISO-8859-1");
      var10000 = var1.get("Retry-After");
      if (var10000 != null) {
         HTTP_STRING_326 = (HttpString)var10000;
      } else {
         HTTP_STRING_326 = new HttpString("Retry-After");
      }

      STATE_BYTES_327 = "Retry-After".getBytes("ISO-8859-1");
      var10000 = var1.get("Retry-After");
      if (var10000 != null) {
         HTTP_STRING_328 = (HttpString)var10000;
      } else {
         HTTP_STRING_328 = new HttpString("Retry-After");
      }

      STATE_BYTES_329 = "Retry-After".getBytes("ISO-8859-1");
      var10000 = var1.get("Retry-After");
      if (var10000 != null) {
         HTTP_STRING_330 = (HttpString)var10000;
      } else {
         HTTP_STRING_330 = new HttpString("Retry-After");
      }

      STATE_BYTES_331 = "Retry-After".getBytes("ISO-8859-1");
      var10000 = var1.get("Retry-After");
      if (var10000 != null) {
         HTTP_STRING_332 = (HttpString)var10000;
      } else {
         HTTP_STRING_332 = new HttpString("Retry-After");
      }

      STATE_BYTES_333 = "Retry-After".getBytes("ISO-8859-1");
      var10000 = var1.get("Retry-After");
      if (var10000 != null) {
         HTTP_STRING_334 = (HttpString)var10000;
      } else {
         HTTP_STRING_334 = new HttpString("Retry-After");
      }

      var10000 = var1.get("S");
      if (var10000 != null) {
         HTTP_STRING_335 = (HttpString)var10000;
      } else {
         HTTP_STRING_335 = new HttpString("S");
      }

      var10000 = var1.get("Se");
      if (var10000 != null) {
         HTTP_STRING_336 = (HttpString)var10000;
      } else {
         HTTP_STRING_336 = new HttpString("Se");
      }

      STATE_BYTES_337 = "Server".getBytes("ISO-8859-1");
      var10000 = var1.get("Server");
      if (var10000 != null) {
         HTTP_STRING_338 = (HttpString)var10000;
      } else {
         HTTP_STRING_338 = new HttpString("Server");
      }

      STATE_BYTES_339 = "Server".getBytes("ISO-8859-1");
      var10000 = var1.get("Server");
      if (var10000 != null) {
         HTTP_STRING_340 = (HttpString)var10000;
      } else {
         HTTP_STRING_340 = new HttpString("Server");
      }

      STATE_BYTES_341 = "Server".getBytes("ISO-8859-1");
      var10000 = var1.get("Server");
      if (var10000 != null) {
         HTTP_STRING_342 = (HttpString)var10000;
      } else {
         HTTP_STRING_342 = new HttpString("Server");
      }

      STATE_BYTES_343 = "Server".getBytes("ISO-8859-1");
      var10000 = var1.get("Server");
      if (var10000 != null) {
         HTTP_STRING_344 = (HttpString)var10000;
      } else {
         HTTP_STRING_344 = new HttpString("Server");
      }

      STATE_BYTES_345 = "Set-Cookie2".getBytes("ISO-8859-1");
      var10000 = var1.get("Set-Cookie2");
      if (var10000 != null) {
         HTTP_STRING_346 = (HttpString)var10000;
      } else {
         HTTP_STRING_346 = new HttpString("Set-Cookie2");
      }

      STATE_BYTES_347 = "Set-Cookie2".getBytes("ISO-8859-1");
      var10000 = var1.get("Set-Cookie2");
      if (var10000 != null) {
         HTTP_STRING_348 = (HttpString)var10000;
      } else {
         HTTP_STRING_348 = new HttpString("Set-Cookie2");
      }

      STATE_BYTES_349 = "Set-Cookie2".getBytes("ISO-8859-1");
      var10000 = var1.get("Set-Cookie2");
      if (var10000 != null) {
         HTTP_STRING_350 = (HttpString)var10000;
      } else {
         HTTP_STRING_350 = new HttpString("Set-Cookie2");
      }

      STATE_BYTES_351 = "Set-Cookie2".getBytes("ISO-8859-1");
      var10000 = var1.get("Set-Cookie2");
      if (var10000 != null) {
         HTTP_STRING_352 = (HttpString)var10000;
      } else {
         HTTP_STRING_352 = new HttpString("Set-Cookie2");
      }

      STATE_BYTES_353 = "Set-Cookie2".getBytes("ISO-8859-1");
      var10000 = var1.get("Set-Cookie2");
      if (var10000 != null) {
         HTTP_STRING_354 = (HttpString)var10000;
      } else {
         HTTP_STRING_354 = new HttpString("Set-Cookie2");
      }

      STATE_BYTES_355 = "Set-Cookie2".getBytes("ISO-8859-1");
      var10000 = var1.get("Set-Cookie2");
      if (var10000 != null) {
         HTTP_STRING_356 = (HttpString)var10000;
      } else {
         HTTP_STRING_356 = new HttpString("Set-Cookie2");
      }

      STATE_BYTES_357 = "Set-Cookie2".getBytes("ISO-8859-1");
      var10000 = var1.get("Set-Cookie2");
      if (var10000 != null) {
         HTTP_STRING_358 = (HttpString)var10000;
      } else {
         HTTP_STRING_358 = new HttpString("Set-Cookie2");
      }

      STATE_BYTES_359 = "Set-Cookie2".getBytes("ISO-8859-1");
      var10000 = var1.get("Set-Cookie2");
      if (var10000 != null) {
         HTTP_STRING_360 = (HttpString)var10000;
      } else {
         HTTP_STRING_360 = new HttpString("Set-Cookie2");
      }

      STATE_BYTES_361 = "Set-Cookie2".getBytes("ISO-8859-1");
      var10000 = var1.get("Set-Cookie2");
      if (var10000 != null) {
         HTTP_STRING_362 = (HttpString)var10000;
      } else {
         HTTP_STRING_362 = new HttpString("Set-Cookie2");
      }

      STATE_BYTES_363 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_364 = (HttpString)var10000;
      } else {
         HTTP_STRING_364 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_365 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_366 = (HttpString)var10000;
      } else {
         HTTP_STRING_366 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_367 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_368 = (HttpString)var10000;
      } else {
         HTTP_STRING_368 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_369 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_370 = (HttpString)var10000;
      } else {
         HTTP_STRING_370 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_371 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_372 = (HttpString)var10000;
      } else {
         HTTP_STRING_372 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_373 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_374 = (HttpString)var10000;
      } else {
         HTTP_STRING_374 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_375 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_376 = (HttpString)var10000;
      } else {
         HTTP_STRING_376 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_377 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_378 = (HttpString)var10000;
      } else {
         HTTP_STRING_378 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_379 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_380 = (HttpString)var10000;
      } else {
         HTTP_STRING_380 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_381 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_382 = (HttpString)var10000;
      } else {
         HTTP_STRING_382 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_383 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_384 = (HttpString)var10000;
      } else {
         HTTP_STRING_384 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_385 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_386 = (HttpString)var10000;
      } else {
         HTTP_STRING_386 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_387 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_388 = (HttpString)var10000;
      } else {
         HTTP_STRING_388 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_389 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_390 = (HttpString)var10000;
      } else {
         HTTP_STRING_390 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_391 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_392 = (HttpString)var10000;
      } else {
         HTTP_STRING_392 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_393 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_394 = (HttpString)var10000;
      } else {
         HTTP_STRING_394 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_395 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_396 = (HttpString)var10000;
      } else {
         HTTP_STRING_396 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_397 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_398 = (HttpString)var10000;
      } else {
         HTTP_STRING_398 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_399 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_400 = (HttpString)var10000;
      } else {
         HTTP_STRING_400 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_401 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_402 = (HttpString)var10000;
      } else {
         HTTP_STRING_402 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_403 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_404 = (HttpString)var10000;
      } else {
         HTTP_STRING_404 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_405 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_406 = (HttpString)var10000;
      } else {
         HTTP_STRING_406 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_407 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_408 = (HttpString)var10000;
      } else {
         HTTP_STRING_408 = new HttpString("Strict-Transport-Security");
      }

      STATE_BYTES_409 = "Strict-Transport-Security".getBytes("ISO-8859-1");
      var10000 = var1.get("Strict-Transport-Security");
      if (var10000 != null) {
         HTTP_STRING_410 = (HttpString)var10000;
      } else {
         HTTP_STRING_410 = new HttpString("Strict-Transport-Security");
      }

      var10000 = var1.get("T");
      if (var10000 != null) {
         HTTP_STRING_411 = (HttpString)var10000;
      } else {
         HTTP_STRING_411 = new HttpString("T");
      }

      var10000 = var1.get("Tr");
      if (var10000 != null) {
         HTTP_STRING_412 = (HttpString)var10000;
      } else {
         HTTP_STRING_412 = new HttpString("Tr");
      }

      var10000 = var1.get("Tra");
      if (var10000 != null) {
         HTTP_STRING_413 = (HttpString)var10000;
      } else {
         HTTP_STRING_413 = new HttpString("Tra");
      }

      STATE_BYTES_414 = "Trailer".getBytes("ISO-8859-1");
      var10000 = var1.get("Trailer");
      if (var10000 != null) {
         HTTP_STRING_415 = (HttpString)var10000;
      } else {
         HTTP_STRING_415 = new HttpString("Trailer");
      }

      STATE_BYTES_416 = "Trailer".getBytes("ISO-8859-1");
      var10000 = var1.get("Trailer");
      if (var10000 != null) {
         HTTP_STRING_417 = (HttpString)var10000;
      } else {
         HTTP_STRING_417 = new HttpString("Trailer");
      }

      STATE_BYTES_418 = "Trailer".getBytes("ISO-8859-1");
      var10000 = var1.get("Trailer");
      if (var10000 != null) {
         HTTP_STRING_419 = (HttpString)var10000;
      } else {
         HTTP_STRING_419 = new HttpString("Trailer");
      }

      STATE_BYTES_420 = "Trailer".getBytes("ISO-8859-1");
      var10000 = var1.get("Trailer");
      if (var10000 != null) {
         HTTP_STRING_421 = (HttpString)var10000;
      } else {
         HTTP_STRING_421 = new HttpString("Trailer");
      }

      STATE_BYTES_422 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_423 = (HttpString)var10000;
      } else {
         HTTP_STRING_423 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_424 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_425 = (HttpString)var10000;
      } else {
         HTTP_STRING_425 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_426 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_427 = (HttpString)var10000;
      } else {
         HTTP_STRING_427 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_428 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_429 = (HttpString)var10000;
      } else {
         HTTP_STRING_429 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_430 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_431 = (HttpString)var10000;
      } else {
         HTTP_STRING_431 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_432 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_433 = (HttpString)var10000;
      } else {
         HTTP_STRING_433 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_434 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_435 = (HttpString)var10000;
      } else {
         HTTP_STRING_435 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_436 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_437 = (HttpString)var10000;
      } else {
         HTTP_STRING_437 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_438 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_439 = (HttpString)var10000;
      } else {
         HTTP_STRING_439 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_440 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_441 = (HttpString)var10000;
      } else {
         HTTP_STRING_441 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_442 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_443 = (HttpString)var10000;
      } else {
         HTTP_STRING_443 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_444 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_445 = (HttpString)var10000;
      } else {
         HTTP_STRING_445 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_446 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_447 = (HttpString)var10000;
      } else {
         HTTP_STRING_447 = new HttpString("Transfer-Encoding");
      }

      STATE_BYTES_448 = "Transfer-Encoding".getBytes("ISO-8859-1");
      var10000 = var1.get("Transfer-Encoding");
      if (var10000 != null) {
         HTTP_STRING_449 = (HttpString)var10000;
      } else {
         HTTP_STRING_449 = new HttpString("Transfer-Encoding");
      }

      var10000 = var1.get("V");
      if (var10000 != null) {
         HTTP_STRING_450 = (HttpString)var10000;
      } else {
         HTTP_STRING_450 = new HttpString("V");
      }

      STATE_BYTES_451 = "Vary".getBytes("ISO-8859-1");
      var10000 = var1.get("Vary");
      if (var10000 != null) {
         HTTP_STRING_452 = (HttpString)var10000;
      } else {
         HTTP_STRING_452 = new HttpString("Vary");
      }

      STATE_BYTES_453 = "Vary".getBytes("ISO-8859-1");
      var10000 = var1.get("Vary");
      if (var10000 != null) {
         HTTP_STRING_454 = (HttpString)var10000;
      } else {
         HTTP_STRING_454 = new HttpString("Vary");
      }

      STATE_BYTES_455 = "Vary".getBytes("ISO-8859-1");
      var10000 = var1.get("Vary");
      if (var10000 != null) {
         HTTP_STRING_456 = (HttpString)var10000;
      } else {
         HTTP_STRING_456 = new HttpString("Vary");
      }

      STATE_BYTES_457 = "Via".getBytes("ISO-8859-1");
      var10000 = var1.get("Via");
      if (var10000 != null) {
         HTTP_STRING_458 = (HttpString)var10000;
      } else {
         HTTP_STRING_458 = new HttpString("Via");
      }

      STATE_BYTES_459 = "Via".getBytes("ISO-8859-1");
      var10000 = var1.get("Via");
      if (var10000 != null) {
         HTTP_STRING_460 = (HttpString)var10000;
      } else {
         HTTP_STRING_460 = new HttpString("Via");
      }

      var10000 = var1.get("W");
      if (var10000 != null) {
         HTTP_STRING_461 = (HttpString)var10000;
      } else {
         HTTP_STRING_461 = new HttpString("W");
      }

      STATE_BYTES_462 = "Warning".getBytes("ISO-8859-1");
      var10000 = var1.get("Warning");
      if (var10000 != null) {
         HTTP_STRING_463 = (HttpString)var10000;
      } else {
         HTTP_STRING_463 = new HttpString("Warning");
      }

      STATE_BYTES_464 = "Warning".getBytes("ISO-8859-1");
      var10000 = var1.get("Warning");
      if (var10000 != null) {
         HTTP_STRING_465 = (HttpString)var10000;
      } else {
         HTTP_STRING_465 = new HttpString("Warning");
      }

      STATE_BYTES_466 = "Warning".getBytes("ISO-8859-1");
      var10000 = var1.get("Warning");
      if (var10000 != null) {
         HTTP_STRING_467 = (HttpString)var10000;
      } else {
         HTTP_STRING_467 = new HttpString("Warning");
      }

      STATE_BYTES_468 = "Warning".getBytes("ISO-8859-1");
      var10000 = var1.get("Warning");
      if (var10000 != null) {
         HTTP_STRING_469 = (HttpString)var10000;
      } else {
         HTTP_STRING_469 = new HttpString("Warning");
      }

      STATE_BYTES_470 = "Warning".getBytes("ISO-8859-1");
      var10000 = var1.get("Warning");
      if (var10000 != null) {
         HTTP_STRING_471 = (HttpString)var10000;
      } else {
         HTTP_STRING_471 = new HttpString("Warning");
      }

      STATE_BYTES_472 = "Warning".getBytes("ISO-8859-1");
      var10000 = var1.get("Warning");
      if (var10000 != null) {
         HTTP_STRING_473 = (HttpString)var10000;
      } else {
         HTTP_STRING_473 = new HttpString("Warning");
      }

      STATE_BYTES_474 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_475 = (HttpString)var10000;
      } else {
         HTTP_STRING_475 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_476 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_477 = (HttpString)var10000;
      } else {
         HTTP_STRING_477 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_478 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_479 = (HttpString)var10000;
      } else {
         HTTP_STRING_479 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_480 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_481 = (HttpString)var10000;
      } else {
         HTTP_STRING_481 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_482 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_483 = (HttpString)var10000;
      } else {
         HTTP_STRING_483 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_484 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_485 = (HttpString)var10000;
      } else {
         HTTP_STRING_485 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_486 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_487 = (HttpString)var10000;
      } else {
         HTTP_STRING_487 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_488 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_489 = (HttpString)var10000;
      } else {
         HTTP_STRING_489 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_490 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_491 = (HttpString)var10000;
      } else {
         HTTP_STRING_491 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_492 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_493 = (HttpString)var10000;
      } else {
         HTTP_STRING_493 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_494 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_495 = (HttpString)var10000;
      } else {
         HTTP_STRING_495 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_496 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_497 = (HttpString)var10000;
      } else {
         HTTP_STRING_497 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_498 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_499 = (HttpString)var10000;
      } else {
         HTTP_STRING_499 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_500 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_501 = (HttpString)var10000;
      } else {
         HTTP_STRING_501 = new HttpString("WWW-Authenticate");
      }

      STATE_BYTES_502 = "WWW-Authenticate".getBytes("ISO-8859-1");
      var10000 = var1.get("WWW-Authenticate");
      if (var10000 != null) {
         HTTP_STRING_503 = (HttpString)var10000;
      } else {
         HTTP_STRING_503 = new HttpString("WWW-Authenticate");
      }

   }

   public HttpResponseParser$$generated() throws  {
   }

   protected final void handleHttpVersion(ByteBuffer var1, ResponseParseState var2, HttpResponseBuilder var3) throws BadRequestException {
      boolean var10;
      if (!var1.hasRemaining()) {
         var10 = false;
      } else {
         int var4;
         int var5;
         HttpString var6;
         byte[] var8;
         label309: {
            StringBuilder var7;
            label283: {
               byte var10000;
               label272: {
                  label284: {
                     label285: {
                        label286: {
                           label287: {
                              label288: {
                                 label289: {
                                    label290: {
                                       label264: {
                                          var7 = var2.stringBuilder;
                                          if ((var4 = var2.parseState) != 0) {
                                             var5 = var2.pos;
                                             var6 = var2.current;
                                             var8 = var2.currentBytes;
                                             switch (var4) {
                                                case -2:
                                                   break label284;
                                                case -1:
                                                   break label272;
                                                case 0:
                                                   break;
                                                case 1:
                                                   break label264;
                                                case 2:
                                                   break label290;
                                                case 3:
                                                   break label289;
                                                case 4:
                                                   break label288;
                                                case 5:
                                                   break label287;
                                                case 6:
                                                   break label286;
                                                case 7:
                                                   break label285;
                                                default:
                                                   throw new RuntimeException("Invalid character");
                                             }
                                          } else {
                                             var5 = 0;
                                             var6 = null;
                                             var8 = null;
                                          }

                                          while(true) {
                                             var10000 = var2.leftOver;
                                             if (var10000 == 0) {
                                                if (!var1.hasRemaining()) {
                                                   break label309;
                                                }

                                                var10000 = var1.get();
                                             } else {
                                                var2.leftOver = 0;
                                             }

                                             if (var10000 == 72) {
                                                var4 = 1;
                                                break;
                                             }

                                             if (var10000 != 32) {
                                                if (var10000 != 13 && var10000 != 10) {
                                                   var4 = -1;
                                                   var7.append("").append((char)var10000);
                                                   break label272;
                                                }

                                                throw new BadRequestException();
                                             }

                                             if (!var1.hasRemaining()) {
                                                break label309;
                                             }
                                          }
                                       }

                                       if (!var1.hasRemaining()) {
                                          break label309;
                                       }

                                       var10000 = var1.get();
                                       if (var10000 != 84) {
                                          if (var10000 == 32) {
                                             var3.setProtocol(HTTP_STRING_3);
                                             var2.leftOver = var10000;
                                             var2.state = 1;
                                             break label283;
                                          }

                                          if (var10000 == 13 || var10000 == 10) {
                                             throw new BadRequestException();
                                          }

                                          var4 = -1;
                                          var7.append("H").append((char)var10000);
                                          break label272;
                                       }

                                       var4 = 2;
                                    }

                                    if (!var1.hasRemaining()) {
                                       break label309;
                                    }

                                    var10000 = var1.get();
                                    if (var10000 != 84) {
                                       if (var10000 == 32) {
                                          var3.setProtocol(HTTP_STRING_4);
                                          var2.leftOver = var10000;
                                          var2.state = 1;
                                          break label283;
                                       }

                                       if (var10000 != 13 && var10000 != 10) {
                                          var4 = -1;
                                          var7.append("HT").append((char)var10000);
                                          break label272;
                                       }

                                       throw new BadRequestException();
                                    }

                                    var4 = 3;
                                 }

                                 if (!var1.hasRemaining()) {
                                    break label309;
                                 }

                                 var10000 = var1.get();
                                 if (var10000 != 80) {
                                    if (var10000 == 32) {
                                       var3.setProtocol(HTTP_STRING_5);
                                       var2.leftOver = var10000;
                                       var2.state = 1;
                                       break label283;
                                    }

                                    if (var10000 == 13 || var10000 == 10) {
                                       throw new BadRequestException();
                                    }

                                    var4 = -1;
                                    var7.append("HTT").append((char)var10000);
                                    break label272;
                                 }

                                 var4 = 4;
                              }

                              if (!var1.hasRemaining()) {
                                 break label309;
                              }

                              var10000 = var1.get();
                              if (var10000 != 47) {
                                 if (var10000 == 32) {
                                    var3.setProtocol(HTTP_STRING_6);
                                    var2.leftOver = var10000;
                                    var2.state = 1;
                                    break label283;
                                 }

                                 if (var10000 != 13 && var10000 != 10) {
                                    var4 = -1;
                                    var7.append("HTTP").append((char)var10000);
                                    break label272;
                                 }

                                 throw new BadRequestException();
                              }

                              var4 = 5;
                           }

                           if (!var1.hasRemaining()) {
                              break label309;
                           }

                           var10000 = var1.get();
                           if (var10000 == 48) {
                              var4 = -2;
                              var6 = HTTP_STRING_9;
                              var8 = STATE_BYTES_8;
                              var5 = 6;
                              break label284;
                           }

                           if (var10000 != 49) {
                              if (var10000 == 32) {
                                 var3.setProtocol(HTTP_STRING_7);
                                 var2.leftOver = var10000;
                                 var2.state = 1;
                                 break label283;
                              }

                              if (var10000 != 13 && var10000 != 10) {
                                 var4 = -1;
                                 var7.append("HTTP/").append((char)var10000);
                                 break label272;
                              }

                              throw new BadRequestException();
                           }

                           var4 = 6;
                        }

                        if (!var1.hasRemaining()) {
                           break label309;
                        }

                        var10000 = var1.get();
                        if (var10000 != 46) {
                           if (var10000 == 32) {
                              var3.setProtocol(HTTP_STRING_14);
                              var2.leftOver = var10000;
                              var2.state = 1;
                              break label283;
                           }

                           if (var10000 == 13 || var10000 == 10) {
                              throw new BadRequestException();
                           }

                           var4 = -1;
                           var7.append("HTTP/1").append((char)var10000);
                           break label272;
                        }

                        var4 = 7;
                     }

                     if (!var1.hasRemaining()) {
                        break label309;
                     }

                     var10000 = var1.get();
                     if (var10000 != 48) {
                        if (var10000 != 49) {
                           if (var10000 == 32) {
                              var3.setProtocol(HTTP_STRING_15);
                              var2.leftOver = var10000;
                              var2.state = 1;
                              break label283;
                           }

                           if (var10000 != 13 && var10000 != 10) {
                              var4 = -1;
                              var7.append("HTTP/1.").append((char)var10000);
                              break label272;
                           }

                           throw new BadRequestException();
                        }

                        var4 = -2;
                        var6 = HTTP_STRING_19;
                        var8 = STATE_BYTES_18;
                        var5 = 8;
                     } else {
                        var4 = -2;
                        var6 = HTTP_STRING_17;
                        var8 = STATE_BYTES_16;
                        var5 = 8;
                     }
                  }

                  while(true) {
                     if (!var1.hasRemaining()) {
                        break label309;
                     }

                     var10000 = var1.get();
                     if (var10000 == 32) {
                        boolean var9 = false;
                        if (var8.length != var5) {
                           var3.setProtocol(new HttpString(var8, 0, var5));
                           var2.leftOver = var10000;
                           var2.state = 1;
                        } else {
                           var3.setProtocol(var6);
                           var2.leftOver = var10000;
                           var2.state = 1;
                        }
                        break label283;
                     }

                     if (var10000 == 13 || var10000 == 10) {
                        throw new BadRequestException();
                     }

                     if (var8.length == var5 || var10000 - var8[var5] != 0) {
                        var4 = -1;
                        var7.append(var6.toString().substring(0, var5)).append((char)var10000);
                        break;
                     }

                     ++var5;
                     if (!var1.hasRemaining()) {
                        break label309;
                     }
                  }
               }

               while(true) {
                  if (!var1.hasRemaining()) {
                     break label309;
                  }

                  var10000 = var1.get();
                  if (var10000 == 32) {
                     var3.setProtocol(new HttpString(var7.toString()));
                     var2.leftOver = var10000;
                     var2.state = 1;
                     break;
                  }

                  if (var10000 == 13 || var10000 == 10) {
                     throw new BadRequestException();
                  }

                  var7.append((char)var10000);
                  if (!var1.hasRemaining()) {
                     var2.parseState = var4;
                     var10 = false;
                     return;
                  }
               }
            }

            var2.pos = 0;
            var2.current = null;
            var2.currentBytes = null;
            var7.setLength(0);
            var2.parseState = 0;
            return;
         }

         var2.pos = var5;
         var2.current = var6;
         var2.currentBytes = var8;
         var2.parseState = var4;
      }
   }
}
