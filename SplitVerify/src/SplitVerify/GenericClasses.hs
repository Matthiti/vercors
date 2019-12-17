{-# OPTIONS_GHC -Wall -Wcompat -Widentities -Wincomplete-record-updates -Wincomplete-uni-patterns -Wpartial-fields -Wredundant-constraints #-}
{-# LANGUAGE TypeOperators, DefaultSignatures, UndecidableInstances #-}
module SplitVerify.GenericClasses where
import RIO
import GHC.Generics

-- This file contains generalisations of structures that are morally fold/map
-- These generalisations are intended to provide suitable defaults for classes

data Phantom k = Phantom

class GFold k b f where
  gfold :: Phantom k -> f a -> b
class GFoldOp k b where
  gop :: Phantom k -> b -> b -> b
  gzero :: Phantom k -> b
class GFoldInstance k b a where
  gfoldInstance :: Phantom k -> a -> b

instance GFoldOp k b => GFold k b U1 where
  gfold f U1 = gzero f
instance (GFold k b a, GFold k b c, GFoldOp k b) => GFold k b (a :*: c) where
  gfold f (x :*: y) = gop f (gfold f x) (gfold f y)
instance (GFold k b a, GFold k b c) => GFold k b (a :+: c) where
  gfold f (L1 x) = gfold f x
  gfold f (R1 x) = gfold f x
instance (GFold k b a) => GFold k b (M1 i c a) where
  gfold f (M1 x) = gfold f x
instance (GFoldInstance k b a) => GFold k b (K1 i a) where
  gfold f (K1 x) = gfoldInstance f x

class FoldGeneric k b a where
  foldGeneric :: Phantom k -> a -> b
instance (GFold k b (Rep a), Generic a) => FoldGeneric k b a where
  --foldGeneric :: FoldGeneric k b a => Phantom k -> a -> b
  foldGeneric x = gfold x . from

class FmapLike k arg f where
   gmap :: Phantom k -> arg -> f a -> f a
class FmapInstance k arg a where
   gmapinstance :: Phantom k -> arg -> a -> a

instance FmapLike x arg U1 where
  gmap _ _ U1 = U1
instance (FmapLike x arg a, FmapLike x arg b) => FmapLike x arg (a :*: b) where
  gmap f arg (x :*: y) = gmap f arg x :*: gmap f arg y
instance (FmapLike x arg a, FmapLike x arg b) => FmapLike x arg (a :+: b) where
  gmap f arg (L1 x) = L1 $ gmap f arg x
  gmap f arg (R1 x) = R1 $ gmap f arg x
instance (FmapLike x arg a) => FmapLike x arg (M1 i c a) where
  gmap f arg (M1 x) = M1 $ gmap f arg x
instance FmapInstance x arg a => FmapLike x arg (K1 i a) where
  gmap f arg (K1 x) = K1 $ gmapinstance f arg x

class (FmapLike k arg (Rep a), Generic a) => MapGeneric k arg a where
instance (FmapLike k arg (Rep a), Generic a) => MapGeneric k arg a where
mapGeneric :: MapGeneric k arg a => Phantom k -> arg -> a -> a
mapGeneric x arg = GHC.Generics.to . gmap x arg . from
