srcdir = ./
top_srcdir = ../../

WGET ?= wget
MPOST ?= mpost
ASY ?= asy
EPSTOPDF ?= epstopdf
PDFLATEX ?= pdflatex
BIBTEX ?= bibtex
SHA1SUM ?= sha1sum
SED ?= sed

THIS_MAKEFILE = $(abspath $(firstword ${MAKEFILE_LIST}))

elito-manual_SRCS = \
	main.tex			\
	version.tex			\
	10-requirements.tex		\
	20-quickstart.tex		\
	21-setup.tex			\
	22-project-customization.tex	\
	A0-logs.tex			\
	B0-bootloaders.tex		\
	C0-dirtrees.tex			\
	hyperref.cfg

SED_CMD = \
	-e 's!@GITCNT@!$(CUR_GIT_CNT)!g' \
	-e 's!@GITREF@!$(CUR_GIT_REVISION)!g' \
	-e 's!@GITREFF@!$(CUR_GIT_REVISION_FULL)!g' \

all:	elito-manual.pdf

clean:
	rm -f *.aux *.dvi *.log *.out *.eps *.toc *.pdf _region_.tex
	rm -rf out

elito-manual.pdf:	${elito-manual_SRCS} Makefile
	mkdir -p out
	while true; do \
		old_sum=`( cd out && cat $(patsubst %.tex,%.aux,$(filter %.tex,$^)) 2>/dev/null ) | ${SHA1SUM}` && \
		env TEXINPUTS=.:$(abspath .): $(MAKE) -f ${THIS_MAKEFILE} -C out .build_latex OUTFILE='$@' SOURCES='$^' MAIN='$<' && \
		new_sum=`( cd out && cat $(patsubst %.tex,%.aux,$(filter %.tex,$^)) 2>/dev/null ) | ${SHA1SUM}` && \
		{ test x"$$old_sum" = x"$$new_sum" && break; } \
	done
	cat out/$(basename $(notdir $<)).pdf > $@

.build_latex:
	${PDFLATEX} ${PDFLATEX_FLAGS} '${MAIN}'

.PHONY: .build_latex

version.tex:%:	%.in Makefile ${top_srcdir}.git-info.sh ${top_srcdir}.git-info.mk
	rm -f $@ $@.tmp
	$(SED) $(SED_CMD) $< >$@.tmp
	mv -f $@.tmp $@
	chmod a-w $@

-include ${top_srcdir}.git-info.mk

${top_srcdir}.git-info.sh ${top_srcdir}.git-info.mk:	${top_srcdir}/scripts/git-post-commit
	cd ${top_srcdir} && ./scripts/git-post-commit
