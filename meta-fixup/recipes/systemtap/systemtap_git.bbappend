EXTRA_AUTORECONF += '--exclude=autopoint'
EXTRA_OECONF += "${docopts}"

docopts = "--disable-docs"
docopts_virtclass-native = "" 
# "--enable-docs" will require additional DEPENDS
