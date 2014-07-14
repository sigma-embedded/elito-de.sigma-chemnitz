EXCLUDE_AUTOPOINT = "true"
EXTRA_OECONF += "${docopts}"

docopts = "--disable-docs"
docopts_virtclass-native = "" 
# "--enable-docs" will require additional DEPENDS
