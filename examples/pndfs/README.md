Installation Instructions
=========================
The VerCors verifier can be downloaded from Git, at https://github.com/utwente-fmt/vercors. One can either manually build VerCors (detailed installation instructions are provided on the Git page), or download the latest release of VerCors as a collection of JARS. For Windows users we recommend to download the latest (JAR) release. In our experience, the performance of VerCors is better on Linux and MacOS X than on Windows.

Instructions for Evaluation
===========================
The root directory contains five different verified versions of NDFS, namely: the sequential version that was ported from the Dafny encoding, the swarmed version of NDFS, the parallel version discussed in Section 3 of the paper, and the two optimised versions discussed in Section 4. These five versions can be verified as follows:

- To verify sequential NDFS (sequential_ndfs.pvl), discussed in Section 2.1 of the paper, run `./unix/bin/vct --silicon path_to/sequential_ndfs.pvl` from a terminal.

- To verify the swarmed version of NDFS (swarmed_ndfs.pvl), discussed in Section 2.2 of the paper, run `./unix/bin/vct --silicon path_to/swarmed_ndfs.pvl` from a terminal.

- To verify parallel NDFS (pndfs.pvl), discussed in Section 3 of the paper, run `./unix/bin/vct --silicon path_to/pndfs.pvl`. Remark: some of the lemma/theorem encodings in `pndfs.pvl` have been commented to speed-up verification and development. These have all been proven; to reproduce this, one can simply uncomment the function body and reverify. However, uncommenting all bodies at the same time results in a time-out. This does not invalidate our results, and the developers of VerCors have been notified of this issue.

- To verify the "early cycle detection" optimisation of PNDFS, discussed in Section 4 of the paper, run `./unix/bin/vct --silicon path_to/pndfs_earlycycledetection.pvl`. The remark given above also applies to this version.

- To verify the "all-red" extension of PNDFS, discussed in Section 4 also, run `./unix/bin/vct --silicon path_to/pndfs_allred.pvl`. The remark given above also applies to this version.
